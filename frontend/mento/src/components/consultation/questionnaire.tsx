import { useEffect, useState } from "react";
import { ArrowLeft, Check, Droplets, Sparkles, Scissors } from "lucide-react";
import type { ConsultationCategory } from "@/types/consultation";

interface QuestionnaireProps {
  answers: string[];
  selectedCategory: ConsultationCategory | null;
  onAnswerChange: (index: number, answer: string) => void;
  onSubmitSurvey: (surveyData: string) => void;
  onPrev: () => void;
  canProceed: boolean;
}

const categoryLabels: Record<NonNullable<ConsultationCategory>, { label: string; icon: typeof Droplets }> = {
  skincare: { label: "스킨 케어", icon: Droplets },
  beauty: { label: "뷰티", icon: Sparkles },
  hair: { label: "헤어", icon: Scissors },
};

const questionsByCategory: Record<NonNullable<ConsultationCategory>, string[]> = {
  skincare: ["현재 피부 고민이나 관심사가 무엇인가요?", "현재 사용 중인 스킨케어 제품이 있다면 알려주세요.", "피부 관련 알레르기나 민감한 성분이 있나요?"],
  beauty: ["평소 메이크업 스타일이나 선호하는 룩이 있나요?", "퍼스널 컬러 진단을 받아보신 적이 있나요?", "메이크업 관련해서 가장 어려운 점이 무엇인가요?"],
  hair: ["현재 헤어 스타일에서 불만족스러운 점이 있나요?", "선호하는 헤어 스타일이나 참고 이미지가 있나요?", "두피나 모발 관련 고민이 있다면 알려주세요."],
};

export function Questionnaire({ answers, selectedCategory, onAnswerChange, onSubmitSurvey, onPrev, canProceed }: QuestionnaireProps) {
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const questions = selectedCategory ? questionsByCategory[selectedCategory] : [];
  const totalQuestions = questions.length;

  const categoryInfo = selectedCategory ? categoryLabels[selectedCategory] : null;
  const CategoryIcon = categoryInfo?.icon;

  const handleNextQuestion = () => {
    if (!currentAnswer.trim()) {
      return;
    }
    if (currentQuestion < totalQuestions - 1) {
      window.scrollTo({ top: 0, behavior: "smooth" });
      setCurrentQuestion((prev) => prev + 1);
    }
  };

  const handlePrevQuestion = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
    if (currentQuestion > 0) {
      setCurrentQuestion((prev) => prev - 1);
    } else {
      onPrev();
    }
  };

  const isLastQuestion = currentQuestion === totalQuestions - 1;
  const currentAnswer = answers[currentQuestion] || "";
  const storageKey = "consultationPreQuestions";

  useEffect(() => {
    if (!questions.length) return;
    if (answers.some((answer) => answer?.trim())) return;

    const saved = localStorage.getItem(storageKey);
    if (!saved) return;

    try {
      const parsed = JSON.parse(saved) as { category: ConsultationCategory | null; items: { question: string; answer: string }[] } | { question: string; answer: string }[];
      const items = Array.isArray(parsed) ? parsed : parsed.items;

      items.forEach((item, index) => {
        if (questions[index] === item.question && item.answer) {
          onAnswerChange(index, item.answer);
        }
      });
    } catch {
      // ignore invalid saved data
    }
  }, [answers, onAnswerChange, questions]);

  const handleSubmitAnswers = () => {
    const payload = questions.map((question, index) => ({
      question,
      answer: answers[index] ?? "",
    }));
    const surveyData = JSON.stringify(payload);
    localStorage.setItem(storageKey, surveyData);
    onSubmitSurvey(surveyData);
  };

  if (!selectedCategory) {
    return null; // 또는 placeholder UI
  }

  return (
    <div className="flex flex-col">
      {/* Header */}
      <div className="flex items-center gap-4">
        {categoryInfo && CategoryIcon && (
          <div className="flex items-center gap-2 rounded-lg bg-primary-100 px-4 py-2">
            <CategoryIcon className="h-5 w-5 text-primary-500" />
            <span className="font-semibold text-text-primary">{categoryInfo.label}</span>
          </div>
        )}
        <h1 className="text-2xl font-bold text-text-primary">상담에 필요한 사전 정보를 입력해 주세요</h1>
      </div>

      {/* Question Card */}
      <div className="mt-8 rounded-2xl border border-border bg-card p-8 shadow-sm">
        {/* Question Number */}
        <div className="mb-6 text-center">
          <span className="text-lg font-semibold text-primary-500">Q{currentQuestion + 1}.</span>
          <span className="ml-2 text-lg text-text-secondary">/ {totalQuestions}</span>
        </div>

        {/* Question Text */}
        <h2 className="mb-8 text-center text-xl font-medium text-text-primary">{questions[currentQuestion]}</h2>

        {/* Text Input */}
        <textarea
          value={currentAnswer}
          onChange={(e) => onAnswerChange(currentQuestion, e.target.value)}
          placeholder="자유롭게 작성해 주세요. 상담사가 더 정확한 조언을 드리는 데 도움이 됩니다."
          className="min-h-[200px] w-full resize-none rounded-xl border border-border bg-background p-4 text-text-primary placeholder:text-text-secondary/50 focus:border-primary-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
        />

        {/* Question Progress Dots */}
        <div className="mt-8 flex items-center justify-center gap-2">
          {questions.map((_, idx) => (
            <button
              key={idx}
              type="button"
              onClick={() => setCurrentQuestion(idx)}
              className={`h-2.5 rounded-full transition-all ${idx === currentQuestion ? "w-8 bg-primary-500" : answers[idx]?.trim() ? "w-2.5 bg-primary-300" : "w-2.5 bg-muted"}`}
              aria-label={`질문 ${idx + 1}로 이동`}
            />
          ))}
        </div>
      </div>

      {/* Navigation Buttons */}
      <div className="mt-12 flex items-center justify-between">
        <button
          type="button"
          onClick={handlePrevQuestion}
          className="flex items-center gap-2 rounded-xl border border-border px-6 py-3 text-base font-semibold text-text-primary transition-colors hover:bg-muted"
        >
          <ArrowLeft className="h-5 w-5" />
          이전 단계
        </button>

        {/* Progress Bar */}
        <div className="hidden flex-1 items-center justify-center px-8 md:flex">
          <div className="h-1.5 w-48 overflow-hidden rounded-full bg-muted">
            <div className="h-full bg-primary-500 transition-all duration-300" style={{ width: `${((currentQuestion + 1) / totalQuestions) * 100}%` }} />
          </div>
        </div>

        {isLastQuestion ? (
          <button
            type="button"
            onClick={handleSubmitAnswers}
            disabled={!canProceed}
            className={`flex items-center gap-2 rounded-xl px-8 py-3 text-base font-semibold transition-all ${
              canProceed ? "bg-primary-500 text-dark-bg hover:bg-primary-400 shadow-lg shadow-primary-500/30" : "cursor-not-allowed bg-muted text-muted-foreground"
            }`}
          >
            설문 완료
            <Check className="h-5 w-5" />
          </button>
        ) : (
          <button
            type="button"
            onClick={handleNextQuestion}
            disabled={!currentAnswer.trim()}
            className={`flex items-center gap-2 rounded-xl px-8 py-3 text-base font-semibold transition-all ${
              currentAnswer.trim() ? "bg-primary-500 text-dark-bg shadow-lg shadow-primary-500/30 hover:bg-primary-400" : "cursor-not-allowed bg-muted text-muted-foreground"
            }`}
          >
            다음 질문
          </button>
        )}
      </div>
    </div>
  );
}

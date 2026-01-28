import { useState } from "react";
import { ChevronDown } from "lucide-react";

const faqs = [
  {
    question: "멘토링은 어떤 방식으로 진행되나요?",
    answer:
      "멘토링은 화상 상담 방식으로만 진행됩니다. 예약된 멘토링 시작 30분 전부터 마이페이지 > 멘토링 관리에서 멘토링 세션에 입장할 수 있는 버튼이 활성화되며, 해당 버튼을 통해 화상 멘토링에 참여하실 수 있습니다.",
  },
  {
    question: "상담 시간은 얼마나 되나요?",
    answer: "모든 멘토링은 1회당 1시간으로 진행됩니다. 상담 시간은 고정되어 있으며, 별도의 시간 단위 선택이나 연장은 제공되지 않습니다.",
  },
  {
    question: "예약 취소는 어떻게 하나요?",
    answer: "예약 취소는 마이페이지 > 멘토링 관리에서 직접 진행하실 수 있습니다. 멘토링 시작 기준 1일 전까지 취소가 가능하며, 이후에는 취소가 제한됩니다.",
  },
  {
    question: "상담 후 추가 질문이 있으면 어떻게 하나요?",
    answer: "멘토링 종료 후 제공되는 AI 요약 리포트를 통해 상담 내용을 다시 확인하실 수 있습니다. 추가적인 상담이나 질문이 필요한 경우에는 새로운 멘토링 세션을 예약해 주세요.",
  },
  {
    question: "멘토의 자격은 어떻게 검증되나요?",
    answer: "모든 멘토는 해당 분야 최소 5년 이상의 경력과 관련 자격증을 보유하고 있습니다. MENTO 팀이 직접 인터뷰와 배경 조사를 진행하여 검증된 전문가만 멘토로 활동합니다.",
  },
];

export function MentoringFaq() {
  const [openIndex, setOpenIndex] = useState<number | null>(0);

  return (
    <section className="bg-background py-16 md:py-24">
      <div className="mx-auto max-w-[800px] px-6">
        {/* Section Header */}
        <div className="mb-12 text-center">
          <p className="mb-3 text-sm font-medium uppercase tracking-wider text-primary-500">FAQ</p>
          <h2 className="mb-4 text-balance text-2xl font-bold text-text-primary md:text-3xl">자주 묻는 질문</h2>
        </div>

        {/* FAQ List */}
        <div className="space-y-4">
          {faqs.map((faq, index) => (
            <div key={index} className="overflow-hidden rounded-xl border border-border bg-background">
              <button
                type="button"
                onClick={() => setOpenIndex(openIndex === index ? null : index)}
                className="flex w-full items-center justify-between px-6 py-4 text-left transition-colors hover:bg-muted/50"
              >
                <span className="font-medium text-text-primary">{faq.question}</span>
                <ChevronDown className={`h-5 w-5 shrink-0 text-text-secondary transition-transform ${openIndex === index ? "rotate-180" : ""}`} />
              </button>
              {openIndex === index && (
                <div className="border-t border-border px-6 py-4">
                  <p className="text-sm leading-relaxed text-text-secondary">{faq.answer}</p>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

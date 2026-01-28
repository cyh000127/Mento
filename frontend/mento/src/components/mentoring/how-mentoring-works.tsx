import { Search, Calendar, MessageCircle, CheckCircle, ArrowRight } from "lucide-react";
import { Link } from "react-router-dom";

const steps = [
  {
    icon: Search,
    title: "유형 선택",
    description: "피부, 뷰티, 헤어 중 상담이 필요한 유형을 선택하고 나에게 맞는 멘토링 분야를 정합니다.",
  },
  {
    icon: Calendar,
    title: "일정 예약",
    description: "원하는 날짜와 시간을 선택하여 멘토링 일정을 간편하게 예약합니다.",
  },
  {
    icon: MessageCircle,
    title: "1:1 상담",
    description: "화상으로 멘토와 깊이 있는 1:1 멘토링을 진행합니다.",
  },
  {
    icon: CheckCircle,
    title: "루틴 완성",
    description: "상담 결과를 바탕으로 나만의 맞춤형 루틴을 완성하세요.",
  },
];

export function HowMentoringWorks() {
  return (
    <section className="bg-muted/30 py-16 md:py-24">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Section Header */}
        <div className="mb-12 text-center">
          <p className="mb-3 text-sm font-medium uppercase tracking-wider text-primary-500">Process</p>
          <h2 className="mb-4 text-balance text-2xl font-bold text-text-primary md:text-3xl">멘토링 진행 방법</h2>
          <p className="mx-auto max-w-xl text-pretty text-text-secondary">간단한 4단계로 전문가의 조언을 받으세요.</p>
        </div>

        {/* Steps */}
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          {steps.map((step, index) => (
            <div key={step.title} className="relative rounded-2xl bg-background p-6 shadow-sm">
              {/* Step Number */}
              <div className="absolute -top-3 right-4 flex h-8 w-8 items-center justify-center rounded-full bg-primary-500 text-sm font-bold text-dark-bg">{index + 1}</div>

              {/* Icon */}
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-primary-100">
                <step.icon className="h-6 w-6 text-primary-500" />
              </div>

              {/* Content */}
              <h3 className="mb-2 text-lg font-semibold text-text-primary">{step.title}</h3>
              <p className="text-sm leading-relaxed text-text-secondary">{step.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

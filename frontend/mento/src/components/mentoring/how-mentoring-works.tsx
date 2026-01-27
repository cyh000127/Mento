import { Search, Calendar, MessageCircle, CheckCircle, ArrowRight } from "lucide-react"
import { Link } from "react-router-dom"

const steps = [
  {
    icon: Search,
    title: "멘토 탐색",
    description: "전문 분야와 리뷰를 확인하고 나에게 맞는 멘토를 선택하세요.",
  },
  {
    icon: Calendar,
    title: "일정 예약",
    description: "멘토의 가능한 시간을 확인하고 원하는 일정을 예약합니다.",
  },
  {
    icon: MessageCircle,
    title: "1:1 상담",
    description: "화상 또는 채팅으로 멘토와 깊이 있는 상담을 진행합니다.",
  },
  {
    icon: CheckCircle,
    title: "루틴 완성",
    description: "상담 결과를 바탕으로 맞춤형 그루밍 루틴을 완성하세요.",
  },
]

export function HowMentoringWorks() {
  return (
    <section className="bg-muted/30 py-16 md:py-24">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Section Header */}
        <div className="mb-12 text-center">
          <p className="mb-3 text-sm font-medium uppercase tracking-wider text-primary-500">
            Process
          </p>
          <h2 className="mb-4 text-balance text-2xl font-bold text-text-primary md:text-3xl">
            멘토링 진행 방법
          </h2>
          <p className="mx-auto max-w-xl text-pretty text-text-secondary">
            간단한 4단계로 전문가의 조언을 받으세요.
          </p>
        </div>

        {/* Steps */}
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          {steps.map((step, index) => (
            <div
              key={step.title}
              className="relative rounded-2xl bg-background p-6 shadow-sm"
            >
              {/* Step Number */}
              <div className="absolute -top-3 right-4 flex h-8 w-8 items-center justify-center rounded-full bg-primary-500 text-sm font-bold text-dark-bg">
                {index + 1}
              </div>

              {/* Icon */}
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-primary-100">
                <step.icon className="h-6 w-6 text-primary-500" />
              </div>

              {/* Content */}
              <h3 className="mb-2 text-lg font-semibold text-text-primary">
                {step.title}
              </h3>
              <p className="text-sm leading-relaxed text-text-secondary">
                {step.description}
              </p>
            </div>
          ))}
        </div>

        {/* CTA Button */}
        <div className="mt-12 flex justify-center">
          <Link
            to="/consultation"
            onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}
            className="inline-flex items-center gap-2 rounded-xl bg-primary-500 px-8 py-3.5 font-medium text-dark-bg shadow-lg shadow-primary-500/25 transition-all hover:bg-primary-400 hover:shadow-xl hover:shadow-primary-500/30"
          >
            멘토링 예약하기
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      </div>
    </section>
  )
}

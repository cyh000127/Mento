import { Package, Brain, MessageCircle, BarChart3 } from "lucide-react"

const features = [
  {
    icon: Package,
    title: "뷰티 인벤토리",
    description: "사용 중인 제품을 한눈에 관리하고 유통기한과 교체 시기를 알림으로 받아보세요.",
    color: "bg-pastel-blue-100",
    iconColor: "text-primary-500",
  },
  {
    icon: Brain,
    title: "AI 스킨 분석",
    description: "AI가 피부 상태를 분석하고 맞춤형 스킨케어 루틴을 제안합니다.",
    color: "bg-pastel-purple-100",
    iconColor: "text-text-primary",
  },
  {
    icon: MessageCircle,
    title: "전문가 멘토링",
    description: "검증된 그루밍 전문가와 1:1 상담으로 개인화된 조언을 받으세요.",
    color: "bg-pastel-green-100",
    iconColor: "text-text-primary",
  },
  {
    icon: BarChart3,
    title: "피부 트래킹",
    description: "일일 피부 상태를 기록하고 장기적인 변화 추이를 확인하세요.",
    color: "bg-primary-100",
    iconColor: "text-primary-500",
  },
]

export function FeaturesSection() {
  return (
    <section className="bg-background py-20 md:py-28">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Section Header */}
        <div className="mb-16 text-center">
          <p className="mb-3 text-sm font-medium uppercase tracking-wider text-primary-500">
            Features
          </p>
          <h2 className="mb-4 text-balance text-3xl font-bold text-text-primary md:text-4xl">
            체계적인 그루밍 관리
          </h2>
          <p className="mx-auto max-w-2xl text-pretty text-text-secondary">
            MENTO는 남성 그루밍에 필요한 모든 기능을 제공합니다.
            <br className="hidden md:block" />
            데이터 기반의 스마트한 자기 관리를 경험하세요.
          </p>
        </div>

        {/* Features Grid */}
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          {features.map((feature) => (
            <div
              key={feature.title}
              className="group rounded-2xl border border-border bg-background p-6 shadow-sm transition-all hover:-translate-y-1 hover:shadow-lg hover:shadow-primary-500/5"
            >
              {/* Icon */}
              <div
                className={`mb-4 inline-flex h-12 w-12 items-center justify-center rounded-xl ${feature.color}`}
              >
                <feature.icon className={`h-6 w-6 ${feature.iconColor}`} />
              </div>

              {/* Content */}
              <h3 className="mb-2 text-lg font-semibold text-text-primary">
                {feature.title}
              </h3>
              <p className="text-sm leading-relaxed text-text-secondary">
                {feature.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

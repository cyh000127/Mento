import { Brain, BarChart3, Bell, Calendar } from "lucide-react"

const features = [
  {
    icon: Brain,
    title: "딥러닝 분석",
    description:
      "최신 딥러닝 기술로 피부 상태를 분석합니다. 수분, 유분, 모공, 주름, 색소침착 등 다양한 지표를 정밀하게 측정합니다.",
    color: "bg-primary-100",
    iconColor: "text-primary-500",
  },
  {
    icon: BarChart3,
    title: "변화 추적",
    description:
      "일일, 주간, 월간 피부 상태 변화를 그래프로 확인하세요. 장기적인 트렌드를 파악하고 케어 효과를 측정할 수 있습니다.",
    color: "bg-pastel-green-100",
    iconColor: "text-text-primary",
  },
  {
    icon: Bell,
    title: "스마트 알림",
    description:
      "피부 상태에 따른 케어 타이밍, 제품 교체 시기, 재구매 알림까지. AI가 적절한 시점에 알림을 보내드립니다.",
    color: "bg-pastel-purple-100",
    iconColor: "text-text-primary",
  },
  {
    icon: Calendar,
    title: "루틴 자동화",
    description:
      "분석 결과를 바탕으로 최적의 일일/주간 루틴을 자동으로 생성합니다. 캘린더와 연동하여 관리하세요.",
    color: "bg-pastel-blue-100",
    iconColor: "text-text-primary",
  },
]

export function AiFeatures() {
  return (
    <section className="bg-muted/30 py-16 md:py-24">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Section Header */}
        <div className="mb-12 text-center">
          <p className="mb-3 text-sm font-medium uppercase tracking-wider text-primary-500">
            AI Features
          </p>
          <h2 className="mb-4 text-balance text-2xl font-bold text-text-primary md:text-3xl">
            AI CARE의 핵심 기능
          </h2>
          <p className="mx-auto max-w-xl text-pretty text-text-secondary">
            데이터 기반의 스마트한 피부 관리를 경험하세요.
          </p>
        </div>

        {/* Features Grid */}
        <div className="grid gap-6 md:grid-cols-2">
          {features.map((feature) => (
            <div
              key={feature.title}
              className="flex gap-4 rounded-2xl border border-border bg-background p-6 shadow-sm transition-all hover:shadow-lg hover:shadow-primary-500/5"
            >
              {/* Icon */}
              <div
                className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-xl ${feature.color}`}
              >
                <feature.icon className={`h-6 w-6 ${feature.iconColor}`} />
              </div>

              {/* Content */}
              <div>
                <h3 className="mb-2 text-lg font-semibold text-text-primary">
                  {feature.title}
                </h3>
                <p className="text-sm leading-relaxed text-text-secondary">
                  {feature.description}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

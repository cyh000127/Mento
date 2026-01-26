import { Link } from "react-router-dom"
import { ArrowRight, Sparkles, Users, BookOpen, Cpu } from "lucide-react"

const services = [
  {
    id: "recommend",
    title: "맞춤 추천",
    subtitle: "Personalized Picks",
    description: "피부 타입, 고민, 예산에 맞는 제품을 AI가 분석하여 추천합니다. 수많은 제품 중 당신에게 딱 맞는 것만 골라드려요.",
    icon: Sparkles,
    href: "/recommend",
    gradient: "from-primary-400 to-primary-500",
    bgColor: "bg-primary-100/50",
  },
  {
    id: "mentoring",
    title: "전문가 멘토링",
    subtitle: "Expert Guidance",
    description: "그루밍 전문가와 1:1 상담을 통해 나만의 루틴을 설계하세요. 피부 관리부터 헤어 스타일링까지 전문적인 조언을 받을 수 있습니다.",
    icon: Users,
    href: "/mentoring",
    gradient: "from-pastel-purple-200 to-pastel-purple-100",
    bgColor: "bg-pastel-purple-100/50",
  },
  {
    id: "guide",
    title: "사용 가이드",
    subtitle: "How to Guide",
    description: "그루밍 입문자를 위한 체계적인 가이드를 제공합니다. 기초부터 심화까지, 단계별로 학습하세요.",
    icon: BookOpen,
    href: "/guide",
    gradient: "from-pastel-green-200 to-pastel-green-100",
    bgColor: "bg-pastel-green-100/50",
  },
  {
    id: "ai-care",
    title: "AI CARE",
    subtitle: "Smart Analysis",
    description: "AI가 매일의 피부 상태를 분석하고 최적의 케어 방법을 제안합니다. 데이터 기반의 정확한 피부 관리를 경험하세요.",
    icon: Cpu,
    href: "/ai-care",
    gradient: "from-pastel-blue-200 to-pastel-blue-100",
    bgColor: "bg-pastel-blue-100/50",
  },
]

export function ServicesSection() {
  return (
    <section className="bg-muted/30 py-20 md:py-28">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Section Header */}
        <div className="mb-16 text-center">
          <p className="mb-3 text-sm font-medium uppercase tracking-wider text-primary-500">
            Services
          </p>
          <h2 className="mb-4 text-balance text-3xl font-bold text-text-primary md:text-4xl">
            핵심 서비스
          </h2>
          <p className="mx-auto max-w-2xl text-pretty text-text-secondary">
            MENTO의 주요 서비스를 살펴보세요.
            <br className="hidden md:block" />
            각 서비스는 남성 그루밍의 특성을 고려하여 설계되었습니다.
          </p>
        </div>

        {/* Services Grid */}
        <div className="grid gap-6 md:grid-cols-2">
          {services.map((service) => (
            <Link
              key={service.id}
              to={service.href}
              className={`group relative overflow-hidden rounded-2xl ${service.bgColor} p-8 transition-all hover:-translate-y-1 hover:shadow-xl hover:shadow-primary-500/5`}
            >
              {/* Background decoration */}
              <div
                className={`absolute -right-10 -top-10 h-40 w-40 rounded-full bg-gradient-to-br ${service.gradient} opacity-20 blur-3xl transition-all group-hover:opacity-30`}
              />

              <div className="relative">
                {/* Icon */}
                <div
                  className={`mb-4 inline-flex h-14 w-14 items-center justify-center rounded-xl bg-gradient-to-br ${service.gradient}`}
                >
                  <service.icon className="h-7 w-7 text-dark-bg" />
                </div>

                {/* Content */}
                <p className="mb-1 text-xs font-medium uppercase tracking-wider text-text-secondary">
                  {service.subtitle}
                </p>
                <h3 className="mb-3 text-xl font-bold text-text-primary">
                  {service.title}
                </h3>
                <p className="mb-6 text-sm leading-relaxed text-text-secondary">
                  {service.description}
                </p>

                {/* Link */}
                <span className="inline-flex items-center gap-2 text-sm font-medium text-primary-500 transition-colors group-hover:text-primary-400">
                  자세히 보기
                  <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
                </span>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </section>
  )
}

import { Link } from "react-router-dom"
import { ArrowRight, Sparkles, Users, BookOpen, Cpu } from "lucide-react"
import dryingHairVideo from "@/assets/videos/drying_hair.mp4"

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
    description: "뷰티 전문가와 1:1 상담을 통해 나만의 루틴을 설계하세요. 피부 관리부터 헤어 스타일링까지 전문적인 조언을 받을 수 있습니다.",
    icon: Users,
    href: "/mentoring",
    gradient: "from-pastel-purple-200 to-pastel-purple-100",
    bgColor: "bg-pastel-purple-100/50",
  },
  {
    id: "guide",
    title: "사용 가이드",
    subtitle: "How to Guide",
    description: "뷰티 입문자를 위한 체계적인 가이드를 제공합니다. 기초부터 심화까지, 단계별로 학습하세요.",
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
    <section className="relative h-screen snap-start snap-always bg-muted/30 overflow-hidden">
      {/* Background video */}
      <video
        autoPlay
        muted
        loop
        playsInline
        src={dryingHairVideo}
        className="absolute inset-0 h-full w-full object-cover opacity-30 pointer-events-none"
      />
      <div className="absolute inset-0 bg-background/60 pointer-events-none" />

      <div className="relative z-10 mx-auto flex h-full max-w-[1200px] flex-col justify-center px-6 py-20">
        {/* Section Header */}
        <div className="mb-20 text-center">
          <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-primary-500/20 bg-primary-100/50 px-4 py-2">
            <span className="h-2 w-2 rounded-full bg-primary-500" />
            <p className="text-sm font-medium text-primary-500">
              Services
            </p>
          </div>
          <h2 className="mb-6 text-balance text-4xl font-bold text-text-primary md:text-5xl">
            핵심 서비스
          </h2>
          <p className="mx-auto max-w-2xl text-pretty text-lg text-text-secondary">
            MENTO의 주요 서비스를 살펴보세요.
            <br className="hidden md:block" />
            각 서비스는 남성 뷰티의 특성을 고려하여 설계되었습니다.
          </p>
        </div>

        {/* Services Grid */}
        <div className="grid gap-8 md:grid-cols-2">
          {services.map((service, index) => (
            <Link
              key={service.id}
              to={service.href}
              className={`group relative overflow-hidden rounded-3xl border border-border/50 ${service.bgColor} p-10 transition-all hover:-translate-y-2 hover:shadow-2xl hover:shadow-primary-500/10 hover:border-primary-500/30`}
              style={{
                animationDelay: `${index * 150}ms`,
              }}
            >
              {/* Background decoration */}
              <div
                className={`absolute -right-16 -top-16 h-64 w-64 rounded-full bg-gradient-to-br ${service.gradient} opacity-10 blur-3xl transition-all duration-500 group-hover:opacity-30 group-hover:scale-110`}
              />

              <div className="relative">
                {/* Icon */}
                <div
                  className={`mb-6 inline-flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br ${service.gradient} shadow-lg transition-transform duration-300 group-hover:scale-110 group-hover:rotate-3`}
                >
                  <service.icon className="h-8 w-8 text-dark-bg" />
                </div>

                {/* Content */}
                <p className="mb-2 text-xs font-semibold uppercase tracking-widest text-text-secondary/80">
                  {service.subtitle}
                </p>
                <h3 className="mb-4 text-2xl font-bold text-text-primary transition-colors group-hover:text-primary-500">
                  {service.title}
                </h3>
                <p className="mb-8 text-base leading-relaxed text-text-secondary">
                  {service.description}
                </p>

                {/* Link */}
                <div className="inline-flex items-center gap-2 text-base font-semibold text-primary-500 transition-all group-hover:gap-3">
                  자세히 보기
                  <ArrowRight className="h-5 w-5 transition-transform group-hover:translate-x-1" />
                </div>
              </div>

              {/* Hover border effect */}
              <div className="absolute inset-0 rounded-3xl opacity-0 ring-2 ring-inset ring-primary-500/50 transition-opacity duration-300 group-hover:opacity-100" />
            </Link>
          ))}
        </div>
      </div>
    </section>
  )
}

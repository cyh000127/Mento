import { Package, Brain, MessageCircle, BarChart3, ChevronDown } from "lucide-react"
import dryingHairVideo from "@/assets/videos/drying_hair.mp4"

const features = [
  {
    icon: Package,
    title: "뷰티 인벤토리",
    description: "사용 중인 제품을 한눈에 관리하고\n교체 시기를 알림으로 받아보세요.",
    color: "bg-pastel-blue-100",
    gradient: "from-pastel-blue-200/20 to-primary-100/20",
    iconColor: "text-primary-500",
  },
  {
    icon: Brain,
    title: "AI 스킨 분석",
    description: "AI가 피부 상태를 분석하고 맞춤형\n스킨케어 루틴을 제안합니다.",
    color: "bg-pastel-purple-100",
    gradient: "from-pastel-purple-200/20 to-pastel-purple-100/20",
    iconColor: "text-text-primary",
  },
  {
    icon: MessageCircle,
    title: "전문가 멘토링",
    description: "검증된 뷰티 전문가와 1:1 상담으로\n개인화된 조언을 받으세요.",
    color: "bg-pastel-green-100",
    gradient: "from-pastel-green-200/20 to-pastel-green-100/20",
    iconColor: "text-text-primary",
  },
  {
    icon: BarChart3,
    title: "사용법 제공",
    description: "처음 사용하는 제품도 걱정 없이\n올바른 사용법을 확인해보세요.",
    color: "bg-primary-100",
    gradient: "from-primary-200/20 to-primary-100/20",
    iconColor: "text-primary-500",
  },
]

export function FeaturesSection() {
  return (
    <section
      data-home-section
      className="relative h-screen snap-start snap-always bg-background overflow-hidden"
    >
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
      {/* Subtle background pattern */}
      <div className="absolute inset-0 bg-gradient-to-b from-muted/20 via-background to-muted/20" />
      
      <div className="relative z-10 mx-auto flex h-full max-w-[1200px] flex-col justify-center px-6 py-20">
        {/* Section Header */}
        <div className="mb-20 text-center">
          <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-primary-500/20 bg-primary-100/50 px-4 py-2">
            <span className="h-2 w-2 rounded-full bg-primary-500" />
            <p className="text-sm font-medium text-primary-500">
              Features
            </p>
          </div>
          <h2 className="mb-6 text-balance text-4xl font-bold text-text-primary md:text-5xl">
            체계적인 뷰티 관리
          </h2>
          <p className="mx-auto max-w-2xl text-pretty text-lg text-text-secondary">
            MENTO는 남성 뷰티에 필요한 모든 기능을 제공합니다.
            <br className="hidden md:block" />
            데이터 기반의 스마트한 자기 관리를 경험하세요.
          </p>
        </div>

        {/* Features Grid */}
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          {features.map((feature, index) => (
            <div
              key={feature.title}
              className="group relative overflow-hidden rounded-2xl border border-border/50 bg-background p-8 shadow-sm transition-all hover:-translate-y-2 hover:shadow-xl hover:shadow-primary-500/10 hover:border-primary-500/30"
              style={{
                animationDelay: `${index * 100}ms`,
              }}
            >
              {/* Background gradient */}
              <div
                className={`absolute inset-0 bg-gradient-to-br ${feature.gradient} opacity-0 transition-opacity duration-300 group-hover:opacity-100`}
              />

              <div className="relative">
                {/* Icon */}
                <div
                  className={`mb-6 inline-flex h-14 w-14 items-center justify-center rounded-xl ${feature.color} transition-transform duration-300 group-hover:scale-110`}
                >
                  <feature.icon className={`h-7 w-7 ${feature.iconColor}`} />
                </div>

                {/* Content */}
                <h3 className="mb-3 text-xl font-bold text-text-primary">
                  {feature.title}
                </h3>
                <p className="whitespace-pre-line text-sm leading-relaxed text-text-secondary">
                  {feature.description}
                </p>
              </div>

              

              {/* Hover indicator */}
              <div className="absolute bottom-0 left-0 right-0 h-1 bg-gradient-to-r from-primary-500 to-primary-300 opacity-0 transition-opacity duration-300 group-hover:opacity-100" />
            </div>
          ))}
        </div>

        <div
          className="animate-fade-in-up mt-4"
          style={{ animationDelay: '0.6s', animationFillMode: 'backwards' }}
        >
          <div className="animate-bounce">
            <ChevronDown className="mx-auto h-8 w-8 text-primary-500" />
          </div>
        </div>
      </div>
    </section>
  )
}

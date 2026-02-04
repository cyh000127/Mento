import dryingHairVideo from "@/assets/videos/drying_hair.mp4"
import { ChevronDown } from "lucide-react"

const steps = [
  {
    number: "01",
    title: "회원가입",
    description: "간편한 소셜 로그인으로 가입하고 기본 피부 정보를 입력하세요.",
  },
  {
    number: "02",
    title: "피부 분석",
    description: "AI가 당신의 피부 타입과 고민을 분석하여 프로필을 생성합니다.",
  },
  {
    number: "03",
    title: "맞춤 추천",
    description: "분석 결과를 바탕으로 최적의 제품과 루틴을 추천받으세요.",
  },
  {
    number: "04",
    title: "지속 관리",
    description: "일일 체크와 멘토링으로 꾸준한 뷰티 습관을 만들어가세요.",
  },
]

export function HowItWorksSection() {
  return (
    <section className="relative h-screen snap-start snap-always bg-background overflow-hidden">
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
      {/* Subtle background gradient */}
      <div className="absolute inset-0 bg-gradient-to-b from-background via-muted/10 to-background" />
      
      <div className="relative z-10 mx-auto flex h-full max-w-[1200px] flex-col justify-center px-6 py-20">
        {/* Section Header */}
        <div className="mb-20 text-center">
          <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-primary-500/20 bg-primary-100/50 px-4 py-2">
            <span className="h-2 w-2 rounded-full bg-primary-500" />
            <p className="text-sm font-medium text-primary-500">
              How It Works
            </p>
          </div>
          <h2 className="mb-6 text-balance text-4xl font-bold text-text-primary md:text-5xl">
            시작하는 방법
          </h2>
          <p className="mx-auto max-w-2xl text-pretty text-lg text-text-secondary">
            MENTO와 함께하는 뷰티 여정은 간단합니다.
            <br className="hidden md:block" />
            네 단계만 따라하면 됩니다.
          </p>
        </div>

        {/* Steps */}
        <div className="relative">
          {/* Connection Line - Desktop */}
          <div className="absolute left-0 right-0 top-20 hidden h-1 bg-gradient-to-r from-transparent via-primary-300/50 to-transparent lg:block" />

          <div className="grid gap-12 md:grid-cols-2 lg:grid-cols-4">
            {steps.map((step, index) => (
              <div 
                key={step.number} 
                className="group relative flex flex-col items-center text-center transition-all hover:-translate-y-2"
                style={{
                  animationDelay: `${index * 100}ms`,
                }}
              >
                {/* Number Badge */}
                <div className="relative mb-8 z-10">
                  <div className="flex h-20 w-20 items-center justify-center rounded-2xl bg-gradient-to-br from-primary-400 to-primary-500 shadow-lg shadow-primary-500/30 transition-all duration-300 group-hover:shadow-2xl group-hover:shadow-primary-500/40 group-hover:scale-110 group-hover:rotate-3">
                    <span className="text-2xl font-bold text-dark-bg">{step.number}</span>
                  </div>
                  {/* Pulse effect */}
                  <div className="absolute inset-0 animate-ping rounded-2xl bg-primary-400 opacity-20" />
                  {/* Glow effect on hover */}
                  <div className="absolute inset-0 rounded-2xl bg-gradient-to-br from-primary-400 to-primary-500 opacity-0 blur-xl transition-opacity duration-300 group-hover:opacity-40" />
                </div>

                {/* Content */}
                <h3 className="mb-3 text-xl font-bold text-text-primary transition-colors group-hover:text-primary-500">
                  {step.title}
                </h3>
                <p className="text-base leading-relaxed text-text-secondary">
                  {step.description}
                </p>

                {/* Arrow for mobile */}
                {index < steps.length - 1 && (
                  <div className="my-6 flex h-10 w-10 items-center justify-center rounded-full bg-primary-100 text-primary-400 md:hidden">
                    <svg
                      className="h-6 w-6"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      aria-hidden="true"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M19 14l-7 7m0 0l-7-7m7 7V3"
                      />
                    </svg>
                  </div>
                )}
              </div>
            ))}
          </div>
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

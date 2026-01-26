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
    description: "일일 체크와 멘토링으로 꾸준한 그루밍 습관을 만들어가세요.",
  },
]

export function HowItWorksSection() {
  return (
    <section className="bg-background py-20 md:py-28">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Section Header */}
        <div className="mb-16 text-center">
          <p className="mb-3 text-sm font-medium uppercase tracking-wider text-primary-500">
            How It Works
          </p>
          <h2 className="mb-4 text-balance text-3xl font-bold text-text-primary md:text-4xl">
            시작하는 방법
          </h2>
          <p className="mx-auto max-w-2xl text-pretty text-text-secondary">
            MENTO와 함께하는 그루밍 여정은 간단합니다.
            <br className="hidden md:block" />
            네 단계만 따라하면 됩니다.
          </p>
        </div>

        {/* Steps */}
        <div className="relative">
          {/* Connection Line - Desktop */}
          <div className="absolute left-0 right-0 top-16 hidden h-0.5 bg-gradient-to-r from-primary-100 via-primary-300 to-primary-100 lg:block" />

          <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-4">
            {steps.map((step, index) => (
              <div key={step.number} className="relative flex flex-col items-center text-center">
                {/* Number Badge */}
                <div className="relative mb-6">
                  <div className="flex h-16 w-16 items-center justify-center rounded-full bg-gradient-to-br from-primary-400 to-primary-500 shadow-lg shadow-primary-500/25">
                    <span className="text-xl font-bold text-dark-bg">{step.number}</span>
                  </div>
                  {/* Pulse effect */}
                  <div className="absolute inset-0 animate-ping rounded-full bg-primary-400 opacity-20" />
                </div>

                {/* Content */}
                <h3 className="mb-2 text-lg font-semibold text-text-primary">
                  {step.title}
                </h3>
                <p className="text-sm leading-relaxed text-text-secondary">
                  {step.description}
                </p>

                {/* Arrow for mobile */}
                {index < steps.length - 1 && (
                  <div className="my-4 flex h-8 w-8 items-center justify-center text-primary-300 md:hidden">
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
      </div>
    </section>
  )
}

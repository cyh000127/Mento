import { Sparkles } from "lucide-react"

export function RecommendHero() {
  return (
    <section className="relative overflow-hidden bg-gradient-to-br from-primary-100/50 via-background to-pastel-purple-100/30">
      <div className="mx-auto max-w-[1200px] px-6 py-16 md:py-24">
        <div className="flex flex-col items-center text-center">
          {/* Badge */}
          <div className="mb-6 inline-flex items-center gap-2 rounded-full bg-primary-500/10 px-4 py-1.5">
            <Sparkles className="h-4 w-4 text-primary-500" />
            <span className="text-sm font-medium text-primary-500">
              AI Powered
            </span>
          </div>

          {/* Title */}
          <h1 className="mb-4 text-balance text-3xl font-bold text-text-primary md:text-4xl lg:text-5xl">
            나만의 맞춤 추천
          </h1>

          {/* Description */}
          <p className="max-w-xl text-pretty text-base leading-relaxed text-text-secondary md:text-lg">
            피부 타입, 고민, 예산을 분석하여 당신에게 딱 맞는 제품을 추천합니다.
            AI가 선별한 최적의 그루밍 아이템을 만나보세요.
          </p>
        </div>
      </div>
    </section>
  )
}

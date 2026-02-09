import { BookOpen } from "lucide-react"

export function GuideHero() {
  return (
    <section className="relative overflow-hidden bg-gradient-to-br from-pastel-green-100/50 via-background to-primary-100/30">
      <div className="mx-auto max-w-[1200px] px-6 py-16 md:py-24">
        <div className="flex flex-col items-center text-center">
          {/* Badge */}
          <div className="mb-6 inline-flex items-center gap-2 rounded-full bg-pastel-green-200 px-4 py-1.5">
            <BookOpen className="h-4 w-4 text-text-primary" />
            <span className="text-sm font-medium text-text-primary">
              Learning Hub
            </span>
          </div>

          {/* Title */}
          <h1 className="mb-4 text-balance text-3xl font-bold text-text-primary md:text-4xl lg:text-5xl">
            그루밍 가이드
          </h1>

          {/* Description */}
          <p className="max-w-xl text-pretty text-base leading-relaxed text-text-secondary md:text-lg">
            그루밍 입문자부터 숙련자까지, 단계별로 학습할 수 있는
            체계적인 가이드를 제공합니다. 기초부터 차근차근 시작해보세요.
          </p>
        </div>
      </div>
    </section>
  )
}

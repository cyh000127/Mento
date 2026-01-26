import { Link } from "react-router-dom"
import { ArrowRight, Sparkles } from "lucide-react"

export function AiCareCta() {
  return (
    <section className="bg-background py-16">
      <div className="mx-auto max-w-[1200px] px-6">
        <div className="relative overflow-hidden rounded-3xl bg-dark-bg p-8 md:p-12">
          {/* Background effects */}
          <div className="absolute -right-20 -top-20 h-60 w-60 rounded-full bg-primary-500/20 blur-3xl" />
          <div className="absolute -bottom-20 -left-20 h-60 w-60 rounded-full bg-primary-400/15 blur-3xl" />

          <div className="relative flex flex-col items-center text-center">
            <div className="mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-primary-500">
              <Sparkles className="h-8 w-8 text-dark-bg" />
            </div>

            <h2 className="mb-4 text-balance text-2xl font-bold text-white md:text-3xl">
              AI와 함께 시작하는 스마트 케어
            </h2>
            <p className="mb-8 max-w-xl text-pretty text-base leading-relaxed text-white/70">
              지금 바로 무료 피부 분석을 시작하고 맞춤형 케어 솔루션을 받아보세요.
              당신의 피부를 가장 잘 아는 AI 파트너가 되어드리겠습니다.
            </p>

            <div className="flex flex-wrap items-center justify-center gap-4">
              <button
                type="button"
                className="inline-flex items-center gap-2 rounded-xl bg-primary-500 px-8 py-3.5 font-medium text-dark-bg shadow-lg shadow-primary-500/25 transition-all hover:bg-primary-400 hover:shadow-xl hover:shadow-primary-500/30"
              >
                무료 분석 시작
                <Sparkles className="h-4 w-4" />
              </button>
              <Link
                to="/recommend"
                className="inline-flex items-center gap-2 rounded-xl border border-white/20 px-8 py-3.5 font-medium text-white transition-all hover:bg-white/10"
              >
                추천 제품 보기
                <ArrowRight className="h-4 w-4" />
              </Link>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

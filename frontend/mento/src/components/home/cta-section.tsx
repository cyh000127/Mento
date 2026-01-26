import { Link } from "react-router-dom"
import { ArrowRight } from "lucide-react"

export function CtaSection() {
  return (
    <section className="bg-dark-bg py-20 md:py-28">
      <div className="mx-auto max-w-[1200px] px-6">
        <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-primary-500/20 to-primary-400/10 p-8 md:p-12">
          {/* Background decoration */}
          <div className="absolute -right-20 -top-20 h-60 w-60 rounded-full bg-primary-500/20 blur-3xl" />
          <div className="absolute -bottom-20 -left-20 h-60 w-60 rounded-full bg-primary-400/15 blur-3xl" />

          <div className="relative flex flex-col items-center text-center">
            <h2 className="mb-4 text-balance text-3xl font-bold text-white md:text-4xl">
              지금 바로 시작하세요
            </h2>
            <p className="mb-8 max-w-xl text-pretty text-base leading-relaxed text-white/70 md:text-lg">
              더 나은 자기 관리를 위한 첫 걸음. MENTO가 당신의 그루밍 파트너가
              되어드리겠습니다.
            </p>

            <div className="flex flex-wrap items-center justify-center gap-4">
              <Link
                to="/recommend"
                className="inline-flex items-center gap-2 rounded-xl bg-primary-500 px-8 py-3.5 font-medium text-dark-bg shadow-lg shadow-primary-500/25 transition-all hover:bg-primary-400 hover:shadow-xl hover:shadow-primary-500/30"
              >
                무료로 시작하기
                <ArrowRight className="h-4 w-4" />
              </Link>
              <Link
                to="/guide"
                className="inline-flex items-center gap-2 rounded-xl border border-white/20 px-8 py-3.5 font-medium text-white transition-all hover:bg-white/10"
              >
                더 알아보기
              </Link>
            </div>

            {/* Stats */}
            <div className="mt-12 grid grid-cols-3 gap-8 border-t border-white/10 pt-8 md:gap-16">
              <div>
                <p className="text-2xl font-bold text-primary-400 md:text-3xl">10K+</p>
                <p className="text-sm text-white/60">활성 사용자</p>
              </div>
              <div>
                <p className="text-2xl font-bold text-primary-400 md:text-3xl">50+</p>
                <p className="text-sm text-white/60">전문 멘토</p>
              </div>
              <div>
                <p className="text-2xl font-bold text-primary-400 md:text-3xl">4.9</p>
                <p className="text-sm text-white/60">평균 평점</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

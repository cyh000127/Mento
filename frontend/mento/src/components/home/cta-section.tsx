import { Link } from "react-router-dom"
import { ArrowRight, Sparkles } from "lucide-react"

export function CtaSection() {
  return (
    <section
      data-home-section
      className="relative h-screen snap-start snap-always bg-dark-bg overflow-hidden"
    >
      {/* Background pattern */}
      <div className="absolute inset-0 bg-gradient-to-b from-dark-bg via-dark-bg/95 to-dark-bg" />
      
      <div className="relative mx-auto flex h-full max-w-[1200px] flex-col justify-center px-6 py-20">
        <div className="relative overflow-hidden rounded-[2rem] bg-gradient-to-br from-primary-500/20 via-primary-400/15 to-primary-300/10 p-12 md:p-16 lg:p-20 border border-primary-500/20">
          {/* Background decoration */}
          <div className="absolute -right-24 -top-24 h-80 w-80 rounded-full bg-primary-500/30 blur-[100px]" />
          <div className="absolute -bottom-24 -left-24 h-80 w-80 rounded-full bg-primary-400/20 blur-[100px]" />
          
          {/* Floating particles effect */}
          <div className="absolute inset-0 overflow-hidden">
            <div className="absolute left-1/4 top-1/3 h-2 w-2 rounded-full bg-primary-400/40 animate-pulse" />
            <div className="absolute right-1/3 top-1/4 h-1.5 w-1.5 rounded-full bg-primary-300/30 animate-pulse delay-75" />
            <div className="absolute left-1/3 bottom-1/3 h-2 w-2 rounded-full bg-primary-500/30 animate-pulse delay-150" />
          </div>

          <div className="relative flex flex-col items-center text-center">
            {/* Icon badge */}
            <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-primary-400 to-primary-500 shadow-lg shadow-primary-500/30">
              <Sparkles className="h-8 w-8 text-dark-bg" />
            </div>

            <h2 className="mb-6 text-balance text-4xl font-bold text-white md:text-5xl lg:text-6xl">
              지금 바로 시작하세요
            </h2>
            <p className="mb-10 max-w-2xl text-pretty text-lg leading-relaxed text-white/80 md:text-xl">
              더 나은 자기 관리를 위한 첫 걸음.
              <br className="hidden md:block" />
              MENTO가 당신의 뷰티 파트너가 되어드리겠습니다.
            </p>

            <div className="flex flex-wrap items-center justify-center gap-4">
              <Link
                to="/recommend"
                className="group inline-flex items-center gap-2 rounded-xl bg-primary-500 px-10 py-4 text-base font-semibold text-dark-bg shadow-lg shadow-primary-500/30 transition-all hover:bg-primary-400 hover:shadow-2xl hover:shadow-primary-500/40 hover:scale-105"
              >
                무료로 시작하기
                <ArrowRight className="h-5 w-5 transition-transform group-hover:translate-x-1" />
              </Link>
              <Link
                to="/guide"
                className="inline-flex items-center gap-2 rounded-xl border-2 border-white/30 bg-white/10 backdrop-blur-sm px-10 py-4 text-base font-semibold text-white transition-all hover:bg-white/20 hover:border-white/50"
              >
                더 알아보기
              </Link>
            </div>

            {/* Stats */}
            <div className="mt-16 grid w-full max-w-3xl grid-cols-3 gap-8 border-t border-white/10 pt-12 md:gap-16">
              <div className="group transition-all hover:-translate-y-1">
                <p className="mb-2 text-3xl font-bold text-primary-400 transition-colors group-hover:text-primary-300 md:text-4xl">
                  10K+
                </p>
                <p className="text-sm text-white/70 md:text-base">활성 사용자</p>
              </div>
              <div className="group transition-all hover:-translate-y-1">
                <p className="mb-2 text-3xl font-bold text-primary-400 transition-colors group-hover:text-primary-300 md:text-4xl">
                  50+
                </p>
                <p className="text-sm text-white/70 md:text-base">전문 멘토</p>
              </div>
              <div className="group transition-all hover:-translate-y-1">
                <p className="mb-2 text-3xl font-bold text-primary-400 transition-colors group-hover:text-primary-300 md:text-4xl">
                  4.9
                </p>
                <p className="text-sm text-white/70 md:text-base">평균 평점</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

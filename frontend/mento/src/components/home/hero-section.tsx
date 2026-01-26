import { Link } from "react-router-dom"
import { ArrowRight, Sparkles, Shield, Users } from "lucide-react"

export function HeroSection() {
  return (
    <section className="relative overflow-hidden bg-background">
      {/* Subtle gradient background */}
      <div className="absolute inset-0 bg-gradient-to-br from-primary-100/50 via-background to-pastel-blue-100/30" />
      
      <div className="relative mx-auto max-w-[1200px] px-6 py-20 md:py-32">
        <div className="grid items-center gap-12 md:grid-cols-2">
          {/* Left Content */}
          <div className="flex flex-col items-start">
            {/* Badge */}
            <div className="mb-6 inline-flex items-center gap-2 rounded-full bg-primary-100 px-4 py-1.5">
              <Sparkles className="h-4 w-4 text-primary-500" />
              <span className="text-sm font-medium text-text-primary">
                AI 기반 스킨케어 관리
              </span>
            </div>

            {/* Headline */}
            <h1 className="mb-6 text-balance text-4xl font-bold leading-tight tracking-tight text-text-primary md:text-5xl lg:text-6xl">
              당신만을 위한
              <br />
              <span className="text-primary-500">그루밍 파트너</span>
            </h1>

            {/* Description */}
            <p className="mb-8 max-w-lg text-pretty text-base leading-relaxed text-text-secondary md:text-lg">
              개인 뷰티 인벤토리 관리부터 AI 스킨케어 분석, 전문가 멘토링까지.
              MENTO와 함께 체계적인 그루밍 루틴을 시작하세요.
            </p>

            {/* CTA Buttons */}
            <div className="flex flex-wrap items-center gap-4">
              <Link
                to="/recommend"
                className="inline-flex items-center gap-2 rounded-xl bg-primary-500 px-6 py-3 font-medium text-dark-bg shadow-lg shadow-primary-500/25 transition-all hover:bg-primary-400 hover:shadow-xl hover:shadow-primary-500/30"
              >
                시작하기
                <ArrowRight className="h-4 w-4" />
              </Link>
              <Link
                to="/guide"
                className="inline-flex items-center gap-2 rounded-xl border border-border bg-background px-6 py-3 font-medium text-text-primary transition-all hover:bg-muted"
              >
                서비스 소개
              </Link>
            </div>

            {/* Trust Badges */}
            <div className="mt-12 flex items-center gap-6">
              <div className="flex items-center gap-2">
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-pastel-green-100">
                  <Shield className="h-5 w-5 text-text-primary" />
                </div>
                <div>
                  <p className="text-sm font-medium text-text-primary">안전한 데이터</p>
                  <p className="text-xs text-text-secondary">개인정보 보호</p>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-pastel-purple-100">
                  <Users className="h-5 w-5 text-text-primary" />
                </div>
                <div>
                  <p className="text-sm font-medium text-text-primary">10,000+</p>
                  <p className="text-xs text-text-secondary">활성 사용자</p>
                </div>
              </div>
            </div>
          </div>

          {/* Right Visual */}
          <div className="relative flex items-center justify-center">
            {/* Background decoration */}
            <div className="absolute h-80 w-80 rounded-full bg-gradient-to-br from-primary-300/40 to-primary-500/20 blur-3xl" />
            
            {/* Main Card */}
            <div className="relative w-full max-w-sm rounded-2xl bg-background p-6 shadow-2xl shadow-primary-500/10">
              {/* Card Header */}
              <div className="mb-6 flex items-center justify-between">
                <div>
                  <p className="text-sm text-text-secondary">오늘의 스킨 컨디션</p>
                  <p className="text-2xl font-bold text-text-primary">87점</p>
                </div>
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-gradient-to-br from-primary-400 to-primary-500">
                  <Sparkles className="h-6 w-6 text-dark-bg" />
                </div>
              </div>

              {/* Progress Bars */}
              <div className="space-y-4">
                <div>
                  <div className="mb-1.5 flex items-center justify-between text-sm">
                    <span className="text-text-secondary">수분</span>
                    <span className="font-medium text-text-primary">92%</span>
                  </div>
                  <div className="h-2 overflow-hidden rounded-full bg-muted">
                    <div className="h-full w-[92%] rounded-full bg-gradient-to-r from-primary-400 to-primary-500" />
                  </div>
                </div>
                <div>
                  <div className="mb-1.5 flex items-center justify-between text-sm">
                    <span className="text-text-secondary">유분</span>
                    <span className="font-medium text-text-primary">78%</span>
                  </div>
                  <div className="h-2 overflow-hidden rounded-full bg-muted">
                    <div className="h-full w-[78%] rounded-full bg-gradient-to-r from-pastel-green-200 to-pastel-green-100" />
                  </div>
                </div>
                <div>
                  <div className="mb-1.5 flex items-center justify-between text-sm">
                    <span className="text-text-secondary">탄력</span>
                    <span className="font-medium text-text-primary">85%</span>
                  </div>
                  <div className="h-2 overflow-hidden rounded-full bg-muted">
                    <div className="h-full w-[85%] rounded-full bg-gradient-to-r from-pastel-purple-200 to-pastel-purple-100" />
                  </div>
                </div>
              </div>

              {/* Card Footer */}
              <div className="mt-6 rounded-xl bg-primary-100/50 p-4">
                <p className="text-sm font-medium text-text-primary">AI 추천</p>
                <p className="mt-1 text-xs text-text-secondary">
                  오늘은 보습 집중 케어가 필요해요. 세라마이드 성분의 앰플을 추천드립니다.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

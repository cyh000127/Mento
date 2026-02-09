import { ArrowRight, Sparkles } from "lucide-react"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { LoginModal } from "@/components/login-modal"
import { useAuthStore } from "@/stores/useAuthStore"

export function CtaSection() {
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn)
  const [isLoginOpen, setIsLoginOpen] = useState(false)
  return (
    <>
      <section
        data-home-section
        className="relative h-screen snap-start snap-always bg-dark-bg overflow-hidden"
      >
        {/* 배경 패턴 */}
        <div className="absolute inset-0 bg-gradient-to-b from-dark-bg via-dark-bg/95 to-dark-bg" />
        
        <div className="relative mx-auto flex h-full max-w-[1200px] flex-col justify-center px-6 py-20">
          <div className="relative overflow-hidden rounded-[2rem] bg-gradient-to-br from-primary-500/20 via-primary-400/15 to-primary-300/10 p-12 md:p-16 lg:p-20 border border-primary-500/20">
            {/* 배경 데코레이션 */}
            <div className="absolute -right-24 -top-24 h-80 w-80 rounded-full bg-primary-500/30 blur-[100px]" />
            <div className="absolute -bottom-24 -left-24 h-80 w-80 rounded-full bg-primary-400/20 blur-[100px]" />
            
            {/* 부유 파티클 효과 */}
            <div className="absolute inset-0 overflow-hidden">
              <div className="absolute left-1/4 top-1/3 h-2 w-2 rounded-full bg-primary-400/40 animate-pulse" />
              <div className="absolute right-1/3 top-1/4 h-1.5 w-1.5 rounded-full bg-primary-300/30 animate-pulse delay-75" />
              <div className="absolute left-1/3 bottom-1/3 h-2 w-2 rounded-full bg-primary-500/30 animate-pulse delay-150" />
            </div>

            <div className="relative flex flex-col items-center text-center">
              {/* 아이콘 뱃지 */}
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

              <div className="animate-fade-in-up mt-6 flex flex-col items-center justify-center gap-3 sm:flex-row" style={{ animationDelay: "0.8s", animationFillMode: "backwards" }}>
                {!isLoggedIn && (
                  <Button size="lg" onClick={() => setIsLoginOpen(true)} className="w-full sm:w-auto">
                    로그인
                    <ArrowRight className="h-5 w-5" />
                  </Button>
                )}
              </div>
            </div>
          </div>
        </div>
      </section>
      <LoginModal isOpen={isLoginOpen} onClose={() => setIsLoginOpen(false)} />
    </>
  )
}

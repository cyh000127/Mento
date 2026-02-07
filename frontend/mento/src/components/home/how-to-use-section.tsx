import { ArrowRight } from "lucide-react"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { useAuthStore } from "@/stores/useAuthStore"
import howToUseImage from "@/assets/images/home/how-to-use.png"

export function HowToUseSection() {
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn)

  return (
    <section
      id="how-to-use-section"
      data-home-section
      className="relative h-screen snap-start snap-always bg-background overflow-hidden"
    >
      <div className="relative z-10 mx-auto flex h-full max-w-[1200px] flex-col justify-center px-6 py-20">
        <div className="mb-20 text-center">
          <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-primary-500/20 bg-primary-100/50 px-4 py-2">
            <span className="h-2 w-2 rounded-full bg-primary-500" />
            <p className="text-sm font-medium text-primary-500">How to Use</p>
          </div>
          <h2 className="mb-6 text-balance text-4xl font-bold text-text-primary md:text-5xl">
            뷰티 루틴을 쉽게 시작하세요
          </h2>
          <p className="mx-auto max-w-2xl text-pretty text-lg text-text-secondary">
            피부 고민에 맞춘 사용법을 단계별로 안내합니다.
            <br className="hidden md:block" />
            제품 선택부터 올바른 적용까지 빠르게 확인하세요.
          </p>
          {isLoggedIn && (
            <div className="mt-6 flex flex-col items-center justify-center gap-3 sm:flex-row">
              <Button asChild size="lg">
                <Link to="/guide">
                  Go to How to Use
                  <ArrowRight className="h-5 w-5" />
                </Link>
              </Button>
            </div>
          )}
        </div>

        <div className="flex justify-center">
          <div className="w-full max-w-5xl overflow-hidden rounded-2xl border border-border/50 bg-background shadow-sm">
            <div className="aspect-[16/5] w-full">
              <img
                src={howToUseImage}
                alt="How to use guide preview"
                className="h-full w-full object-contain"
                loading="lazy"
              />
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

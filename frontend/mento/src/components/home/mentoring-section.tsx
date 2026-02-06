import { ArrowRight } from "lucide-react"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { useAuthStore } from "@/stores/useAuthStore"
import mentoringImage from "@/assets/images/home/mentoring-intro.png"

export function MentoringSection() {
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn)

  return (
    <section
      id="mentoring-section"
      data-home-section
      className="relative h-screen snap-start snap-always bg-background overflow-hidden"
    >
      <div className="relative z-10 mx-auto flex h-full max-w-[1200px] flex-col justify-center px-6 py-20">
        <div className="mb-12 text-center">
          <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-primary-500/20 bg-primary-100/50 px-4 py-2">
            <span className="h-2 w-2 rounded-full bg-primary-500" />
            <p className="text-sm font-medium text-primary-500">Mentoring</p>
          </div>
          <h2 className="mb-6 text-balance text-4xl font-bold text-text-primary md:text-5xl">
            전문가와 함께하는 1:1 멘토링
          </h2>
          <p className="mx-auto max-w-2xl text-pretty text-lg text-text-secondary">
            검증된 뷰티 전문가와 실시간 상담으로 피부 고민을 해결하세요.
            <br className="hidden md:block" />
            루틴부터 제품 선택까지 맞춤형 조언을 제공합니다.
          </p>
          {isLoggedIn && (
            <div className="mt-6 flex justify-center">
              <Button asChild size="lg">
                <Link to="/mentoring">
                  Go to Mentoring
                  <ArrowRight className="h-5 w-5" />
                </Link>
              </Button>
            </div>
          )}
        </div>

        <div className="mx-auto w-full max-w-4xl overflow-hidden rounded-2xl border border-border/50 bg-background shadow-sm">
          <div className="aspect-[16/5] w-full">
            <img
              src={mentoringImage}
              alt="Mentoring categories"
              className="h-full w-full object-contain"
              loading="lazy"
            />
          </div>
        </div>
      </div>
    </section>
  )
}

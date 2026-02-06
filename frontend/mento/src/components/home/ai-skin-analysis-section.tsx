import { useState } from "react"
import { ArrowRight } from "lucide-react"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { LoginModal } from "@/components/login-modal"
import { useAuthStore } from "@/stores/useAuthStore"
import aiSkinAnalysisImage from "@/assets/images/home/ai-skin-analysis.png"

export function AiSkinAnalysisSection() {
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn)
  const [isLoginOpen, setIsLoginOpen] = useState(false)

  return (
    <>
      <section
        id="ai-skin-analysis-section"
        data-home-section
        className="relative h-screen snap-start snap-always bg-background overflow-hidden"
      >
        <div className="relative z-10 mx-auto flex h-full max-w-[1200px] flex-col justify-center px-6 py-20">
          <div className="grid items-center gap-10 lg:grid-cols-2">
            <div className="space-y-6">
              <h2 className="text-balance text-4xl font-bold text-text-primary md:text-5xl lg:text-6xl">
                AI Skin Analysis in seconds
              </h2>
              <p className="text-pretty text-lg leading-relaxed text-text-secondary md:text-xl">
                Upload a photo to get AI-based skin insights across moisture, pores, wrinkles, pigmentation, and elasticity.
                <br className="hidden md:block" />
                Fast, easy, and personalized analysis that keeps your routine on track.
              </p>
              <div className="flex flex-col items-start gap-3 sm:flex-row">
                {isLoggedIn ? (
                  <Button asChild size="lg">
                    <Link to="/ai-care">
                      Start AI Skin Analysis
                      <ArrowRight className="h-5 w-5" />
                    </Link>
                  </Button>
                ) : (
                  <Button size="lg" onClick={() => setIsLoginOpen(true)}>
                    Log in to use AI Skin Analysis
                    <ArrowRight className="h-5 w-5" />
                  </Button>
                )}
              </div>
            </div>

            <div className="overflow-hidden rounded-2xl border border-border/50 bg-background shadow-sm">
              <div className="aspect-[4/3] w-full">
                <img
                  src={aiSkinAnalysisImage}
                  alt="AI skin analysis result"
                  className="h-full w-full object-contain"
                  loading="lazy"
                />
              </div>
            </div>
          </div>
        </div>
      </section>
      <LoginModal isOpen={isLoginOpen} onClose={() => setIsLoginOpen(false)} />
    </>
  )
}

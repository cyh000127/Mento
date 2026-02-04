import { useEffect } from "react"
import { HeroSection } from "@/components/home/hero-section"
import { FeaturesSection } from "@/components/home/features-section"
import { HowItWorksSection } from "@/components/home/how-it-works-section"
import { CtaSection } from "@/components/home/cta-section"
import { useAuthStore } from "@/stores/useAuthStore"

export default function HomePage() {
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn)

  useEffect(() => {
    if (!isLoggedIn) {
      document.documentElement.dataset.hideHeader = "true"
    } else {
      delete document.documentElement.dataset.hideHeader
    }

    return () => {
      delete document.documentElement.dataset.hideHeader
    }
  }, [isLoggedIn])

  const scrollContainerHeight = isLoggedIn ? "h-[calc(100vh-3.5rem)]" : "h-screen"

  return (
    <>
      <style>{`
        [data-home-scroll] {
          scrollbar-width: none;
          -ms-overflow-style: none;
          overscroll-behavior-y: contain;
        }
        [data-home-scroll]::-webkit-scrollbar {
          display: none;
          width: 0;
          height: 0;
        }
      `}</style>
      <div
        className={`${scrollContainerHeight} overflow-y-scroll snap-y snap-mandatory scroll-smooth overscroll-y-contain`}
        data-home-scroll
      >
      {/* Hero with 3 scenes */}
      <HeroSection />

      {/* Additional sections */}
      <FeaturesSection />
      <HowItWorksSection />
      <CtaSection />
      </div>
    </>
  )
}

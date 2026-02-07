import { useCallback, useEffect, useLayoutEffect, useRef, useState } from "react"
import { HeroSection } from "@/components/home/hero-section"
import { InventorySection } from "@/components/home/inventory-section"
import { AiSkinAnalysisSection } from "@/components/home/ai-skin-analysis-section"
import { HowToUseSection } from "@/components/home/how-to-use-section"
import { HowItWorksSection } from "@/components/home/how-it-works-section"
import { CtaSection } from "@/components/home/cta-section"
import { useAuthStore } from "@/stores/useAuthStore"
import { MentoringSection } from "@/components/home/mentoring-section"

if (typeof window !== "undefined" && "scrollRestoration" in history) {
  history.scrollRestoration = "manual"
}

export default function HomePage() {
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn)
  const scrollContainerRef = useRef<HTMLDivElement | null>(null)
  const [showIntro, setShowIntro] = useState(() => {
    if (typeof window === "undefined") return true
    return localStorage.getItem("hasVisitedHome") !== "true"
  })

  const resetScroll = useCallback(() => {
    const container = scrollContainerRef.current
    if (container) {
      container.scrollTo({ top: 0, left: 0, behavior: "auto" })
    } else {
      window.scrollTo({ top: 0, left: 0, behavior: "auto" })
    }
  }, [])

  const handleIntroComplete = useCallback(() => {
    localStorage.setItem("hasVisitedHome", "true")
    setShowIntro(false)
  }, [])

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

  useEffect(() => {
    if (showIntro) {
      document.body.style.overflow = "hidden"
    } else {
      document.body.style.overflow = "auto"
    }

    return () => {
      document.body.style.overflow = "auto"
    }
  }, [showIntro])

  useEffect(() => {
    const handlePageShow = () => resetScroll()
    window.addEventListener("pageshow", handlePageShow)
    return () => window.removeEventListener("pageshow", handlePageShow)
  }, [resetScroll])

  useLayoutEffect(() => {
    resetScroll()
  }, [isLoggedIn, resetScroll])

  useLayoutEffect(() => {
    if (showIntro) {
      resetScroll()
      return
    }

    requestAnimationFrame(() => {
      resetScroll()
    })
  }, [showIntro, resetScroll])

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
        className={`${scrollContainerHeight} ${showIntro ? "overflow-hidden snap-none scroll-auto" : "overflow-y-scroll snap-y snap-mandatory scroll-smooth"} overscroll-y-contain`}
        data-home-scroll
        ref={scrollContainerRef}
      >
      {/* 히어로 섹션 */}
      <HeroSection showIntro={showIntro} onIntroComplete={handleIntroComplete} />

      {/* 추가 섹션 */}
      <InventorySection />
      <MentoringSection />
      <AiSkinAnalysisSection />
      <HowToUseSection />
      <HowItWorksSection />
      <CtaSection />
      </div>
    </>
  )
}

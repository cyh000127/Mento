import { useCallback, useEffect, useLayoutEffect, useRef, useState } from "react"
import { HeroSection } from "@/components/home/hero-section"
import { FeaturesSection } from "@/components/home/features-section"
import { HowItWorksSection } from "@/components/home/how-it-works-section"
import { CtaSection } from "@/components/home/cta-section"
import { useAuthStore } from "@/stores/useAuthStore"

if (typeof window !== "undefined" && "scrollRestoration" in history) {
  history.scrollRestoration = "manual"
}

export default function HomePage() {
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn)
  const scrollContainerRef = useRef<HTMLDivElement | null>(null)
  const [snapEnabled, setSnapEnabled] = useState(false)
  const [isScrollReady, setIsScrollReady] = useState(false)

  const resetScroll = useCallback(() => {
    const container = scrollContainerRef.current
    if (container) {
      container.scrollTo({ top: 0, left: 0, behavior: "auto" })
    } else {
      window.scrollTo({ top: 0, left: 0, behavior: "auto" })
    }
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

  useLayoutEffect(() => {
    setSnapEnabled(false)
    setIsScrollReady(false)
    resetScroll()
    const rafId = requestAnimationFrame(() => {
      resetScroll()
      setSnapEnabled(true)
      setIsScrollReady(true)
    })
    return () => cancelAnimationFrame(rafId)
  }, [resetScroll])

  useEffect(() => {
    const handlePageShow = () => resetScroll()
    window.addEventListener("pageshow", handlePageShow)
    return () => window.removeEventListener("pageshow", handlePageShow)
  }, [resetScroll])

  useLayoutEffect(() => {
    setSnapEnabled(false)
    setIsScrollReady(false)
    resetScroll()
    const rafId = requestAnimationFrame(() => {
      resetScroll()
      setSnapEnabled(true)
      setIsScrollReady(true)
    })
    return () => cancelAnimationFrame(rafId)
  }, [isLoggedIn, resetScroll])

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
        className={`${scrollContainerHeight} overflow-y-scroll ${snapEnabled ? "snap-y snap-mandatory" : "snap-none"} ${isScrollReady ? "opacity-100" : "opacity-0"} ${isScrollReady ? "scroll-smooth" : "scroll-auto"} overscroll-y-contain transition-opacity duration-150`}
        data-home-scroll
        ref={scrollContainerRef}
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

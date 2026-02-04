import { HeroSection } from "@/components/home/hero-section"
import { FeaturesSection } from "@/components/home/features-section"
import { HowItWorksSection } from "@/components/home/how-it-works-section"
import { CtaSection } from "@/components/home/cta-section"

export default function HomePage() {
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
        className="h-screen overflow-y-scroll snap-y snap-mandatory scroll-smooth overscroll-y-contain"
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

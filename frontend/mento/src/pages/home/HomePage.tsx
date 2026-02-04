import { HeroSection } from "@/components/home/hero-section"
import { FeaturesSection } from "@/components/home/features-section"
import { ServicesSection } from "@/components/home/services-section"
import { HowItWorksSection } from "@/components/home/how-it-works-section"
import { CtaSection } from "@/components/home/cta-section"

export default function HomePage() {
  return (
    <div
      className="h-screen overflow-y-scroll snap-y snap-mandatory scroll-smooth"
      data-home-scroll
    >
      {/* Hero with 3 scenes */}
      <HeroSection />

      {/* Additional sections */}
      <FeaturesSection />
      <ServicesSection />
      <HowItWorksSection />
      <CtaSection />
    </div>
  )
}

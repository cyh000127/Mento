import { HeroSection } from "@/components/home/hero-section"
import { FeaturesSection } from "@/components/home/features-section"
import { ServicesSection } from "@/components/home/services-section"
import { HowItWorksSection } from "@/components/home/how-it-works-section"
import { CtaSection } from "@/components/home/cta-section"

export default function HomePage() {
  return (
    <div className="scroll-smooth">
      {/* Hero with 3 scenes */}
      <HeroSection />
      
      {/* Additional sections */}
      <div className="relative z-10 bg-background">
        <FeaturesSection />
        <ServicesSection />
        <HowItWorksSection />
        <CtaSection />
      </div>
    </div>
  )
}

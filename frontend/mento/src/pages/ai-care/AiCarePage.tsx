import { AiCareHero } from "@/components/ai-care/ai-care-hero"
import { AiFeatures } from "@/components/ai-care/ai-features"
import { SkinAnalysis } from "@/components/ai-care/skin-analysis"
import { AiCareCta } from "@/components/ai-care/ai-care-cta"

export default function AiCarePage() {
  return (
    <>
      <AiCareHero />
      <AiFeatures />
      <SkinAnalysis />
      <AiCareCta />
    </>
  )
}

import { FaceAreaSelector } from "./FaceAreaSelector"
import { StepGuide } from "./StepGuide"

interface Step {
  number: number
  title: string
  description: string
}

interface UsageGuideContainerProps {
  productType: string
  selectedArea: string
  onAreaSelect: (area: string) => void
  steps: Step[]
}

export function UsageGuideContainer({
  productType,
  selectedArea,
  onAreaSelect,
  steps,
}: UsageGuideContainerProps) {
  return (
    <section className="bg-muted/30 rounded-2xl p-6 md:p-8">
      <h2 className="text-2xl font-bold text-text-primary mb-8">
        {productType} 사용 가이드
      </h2>

      <div className="grid lg:grid-cols-2 gap-8 lg:gap-12">
        {/* Left: Face Area Selector */}
        <div>
          <FaceAreaSelector
            selectedArea={selectedArea}
            onAreaSelect={onAreaSelect}
          />
        </div>

        {/* Right: Step Guide */}
        <div>
          <StepGuide steps={steps} />
        </div>
      </div>
    </section>
  )
}

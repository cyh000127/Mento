import { Check } from "lucide-react"

interface Step {
  id: number
  label: string
}

interface StepIndicatorProps {
  steps: Step[]
  currentStep: number
}


const stepLabels = ["분야 선택", "일정 선택", "사전 설문"]

export function StepIndicator({ steps, currentStep }: StepIndicatorProps) {
  return (
    <div className="flex items-center justify-center">
      <div className="flex items-center gap-2">
        {steps.map((step, index) => {
          const stepNumber = step.id
          const isCompleted = stepNumber < currentStep
          const isActive = stepNumber === currentStep

          return (
            <div key={step.id} className="flex items-center">
              <div className="flex flex-col items-center">
                <div
                  className={`flex h-10 w-10 items-center justify-center rounded-full text-sm font-semibold ${
                    isCompleted
                      ? "bg-primary-500 text-dark-bg"
                      : isActive
                        ? "bg-primary-500 text-dark-bg shadow-lg"
                        : "bg-muted text-muted-foreground"
                  }`}
                >
                  {isCompleted ? "✓" : stepNumber}
                </div>

                <span className="mt-2 text-xs font-medium">
                  {step.label}
                </span>
              </div>

              {index < steps.length - 1 && (
                <div
                  className={`mx-4 h-0.5 w-16 ${
                    isCompleted ? "bg-primary-500" : "bg-muted"
                  }`}
                />
              )}
            </div>
          )
        })}
      </div>
    </div>
  )
}


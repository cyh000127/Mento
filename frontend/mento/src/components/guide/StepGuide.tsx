interface Step {
  number: number
  title: string
  description: string
}

interface StepGuideProps {
  steps: Step[]
}

export function StepGuide({ steps }: StepGuideProps) {
  return (
    <div className="space-y-4">
      <h3 className="text-lg font-semibold text-text-primary mb-6">
        사용 방법
      </h3>

      <div className="space-y-3">
        {steps.map((step) => (
          <div
            key={step.number}
            className="bg-background border border-border rounded-xl p-5 hover:border-primary-300 hover:shadow-sm transition-all duration-200"
          >
            <div className="flex gap-4">
              {/* Step Number Badge */}
              <div className="flex-shrink-0">
                <div className="h-10 w-10 rounded-full bg-primary-500 flex items-center justify-center">
                  <span className="text-white font-bold text-lg">
                    {step.number}
                  </span>
                </div>
              </div>

              {/* Step Content */}
              <div className="flex-1 pt-1">
                <h4 className="text-base font-semibold text-text-primary mb-2">
                  {step.title}
                </h4>
                <p className="text-sm text-text-secondary leading-relaxed">
                  {step.description}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Additional Tips */}
      <div className="mt-6 p-4 bg-pastel-blue-100 rounded-lg border border-pastel-blue-200">
        <p className="text-sm text-text-secondary">
          <span className="font-semibold text-primary-500">💡 Tip:</span>{" "}
          제품은 소량을 여러 번 덧바르는 것이 한 번에 많이 바르는 것보다 효과적입니다.
        </p>
      </div>
    </div>
  )
}

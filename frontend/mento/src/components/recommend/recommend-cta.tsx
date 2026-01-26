import { Link } from "react-router-dom"
import { Brain, ArrowRight } from "lucide-react"

export function RecommendCta() {
  return (
    <section className="bg-muted/30 py-16">
      <div className="mx-auto max-w-[1200px] px-6">
        <div className="flex flex-col items-center justify-between gap-8 rounded-2xl bg-gradient-to-r from-primary-100 to-pastel-blue-100 p-8 md:flex-row md:p-12">
          {/* Content */}
          <div className="flex items-center gap-6">
            <div className="hidden h-16 w-16 items-center justify-center rounded-2xl bg-primary-500 md:flex">
              <Brain className="h-8 w-8 text-dark-bg" />
            </div>
            <div>
              <h3 className="mb-2 text-xl font-bold text-text-primary md:text-2xl">
                더 정확한 추천을 원하시나요?
              </h3>
              <p className="text-text-secondary">
                AI 피부 분석을 통해 당신의 피부 상태를 정밀하게 측정하고
                <br className="hidden md:block" />
                더욱 맞춤화된 제품 추천을 받아보세요.
              </p>
            </div>
          </div>

          {/* CTA Button */}
          <Link
            to="/ai-care"
            className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-primary-500 px-8 py-3.5 font-medium text-dark-bg shadow-lg shadow-primary-500/25 transition-all hover:bg-primary-400 hover:shadow-xl hover:shadow-primary-500/30 md:w-auto"
          >
            AI 분석 시작
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      </div>
    </section>
  )
}

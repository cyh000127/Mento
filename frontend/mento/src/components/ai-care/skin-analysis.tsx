import { useState } from "react"
import { Upload, Camera, Sparkles, ChevronRight } from "lucide-react"

export function SkinAnalysis() {
  const [, setStep] = useState(1)
  const [analysisComplete, setAnalysisComplete] = useState(false)

  const handleStartAnalysis = () => {
    setStep(2)
    // Simulate analysis
    setTimeout(() => {
      setAnalysisComplete(true)
    }, 2000)
  }

  return (
    <section className="bg-background py-16 md:py-24">
      <div className="mx-auto max-w-[1200px] px-6">
        <div className="grid items-start gap-12 lg:grid-cols-2">
          {/* Left - Upload Area */}
          <div>
            <h2 className="mb-4 text-2xl font-bold text-text-primary md:text-3xl">
              피부 분석 시작하기
            </h2>
            <p className="mb-8 text-text-secondary">
              사진을 업로드하면 AI가 피부 상태를 분석하여 맞춤 솔루션을 제안합니다.
            </p>

            {/* Upload Box */}
            <div className="mb-6 rounded-2xl border-2 border-dashed border-primary-300 bg-primary-100/30 p-8 text-center transition-all hover:border-primary-400 hover:bg-primary-100/50">
              <div className="mb-4 flex justify-center">
                <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary-500">
                  <Camera className="h-8 w-8 text-dark-bg" />
                </div>
              </div>
              <p className="mb-2 font-medium text-text-primary">
                피부 사진 업로드
              </p>
              <p className="mb-4 text-sm text-text-secondary">
                JPG, PNG 파일 (최대 10MB)
              </p>
              <div className="flex flex-wrap justify-center gap-3">
                <button
                  type="button"
                  className="inline-flex items-center gap-2 rounded-lg bg-primary-500 px-4 py-2 text-sm font-medium text-dark-bg transition-colors hover:bg-primary-400"
                >
                  <Upload className="h-4 w-4" />
                  파일 선택
                </button>
                <button
                  type="button"
                  className="inline-flex items-center gap-2 rounded-lg border border-border bg-background px-4 py-2 text-sm font-medium text-text-primary transition-colors hover:bg-muted"
                >
                  <Camera className="h-4 w-4" />
                  촬영하기
                </button>
              </div>
            </div>

            {/* Guidelines */}
            <div className="rounded-xl bg-muted/50 p-4">
              <p className="mb-2 text-sm font-medium text-text-primary">
                정확한 분석을 위한 팁
              </p>
              <ul className="space-y-1 text-sm text-text-secondary">
                <li>- 자연광 아래에서 촬영해주세요</li>
                <li>- 메이크업 없는 맨 얼굴이 좋습니다</li>
                <li>- 정면과 양 측면 사진을 함께 업로드하면 더 정확해요</li>
              </ul>
            </div>
          </div>

          {/* Right - Analysis Result Preview */}
          <div className="rounded-2xl border border-border bg-background p-6 shadow-lg">
            {!analysisComplete ? (
              <div className="flex h-full flex-col items-center justify-center py-12 text-center">
                <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-muted">
                  <Sparkles className="h-10 w-10 text-text-secondary" />
                </div>
                <h3 className="mb-2 text-lg font-semibold text-text-primary">
                  분석 결과가 여기에 표시됩니다
                </h3>
                <p className="mb-6 text-sm text-text-secondary">
                  사진을 업로드하면 AI가 피부 상태를 분석합니다
                </p>
                <button
                  type="button"
                  onClick={handleStartAnalysis}
                  className="inline-flex items-center gap-2 rounded-lg bg-primary-500 px-6 py-3 font-medium text-dark-bg transition-colors hover:bg-primary-400"
                >
                  <Sparkles className="h-4 w-4" />
                  샘플 분석 보기
                </button>
              </div>
            ) : (
              <div>
                {/* Score Header */}
                <div className="mb-6 flex items-center justify-between">
                  <div>
                    <p className="text-sm text-text-secondary">피부 종합 점수</p>
                    <p className="text-3xl font-bold text-text-primary">87점</p>
                  </div>
                  <div className="flex h-16 w-16 items-center justify-center rounded-full bg-gradient-to-br from-primary-400 to-primary-500">
                    <Sparkles className="h-8 w-8 text-dark-bg" />
                  </div>
                </div>

                {/* Analysis Results */}
                <div className="mb-6 space-y-4">
                  <div>
                    <div className="mb-1.5 flex items-center justify-between text-sm">
                      <span className="text-text-secondary">수분도</span>
                      <span className="font-medium text-text-primary">92%</span>
                    </div>
                    <div className="h-2.5 overflow-hidden rounded-full bg-muted">
                      <div className="h-full w-[92%] rounded-full bg-gradient-to-r from-primary-400 to-primary-500" />
                    </div>
                  </div>
                  <div>
                    <div className="mb-1.5 flex items-center justify-between text-sm">
                      <span className="text-text-secondary">유분 밸런스</span>
                      <span className="font-medium text-text-primary">78%</span>
                    </div>
                    <div className="h-2.5 overflow-hidden rounded-full bg-muted">
                      <div className="h-full w-[78%] rounded-full bg-gradient-to-r from-pastel-green-200 to-pastel-green-100" />
                    </div>
                  </div>
                  <div>
                    <div className="mb-1.5 flex items-center justify-between text-sm">
                      <span className="text-text-secondary">모공 상태</span>
                      <span className="font-medium text-text-primary">85%</span>
                    </div>
                    <div className="h-2.5 overflow-hidden rounded-full bg-muted">
                      <div className="h-full w-[85%] rounded-full bg-gradient-to-r from-pastel-purple-200 to-pastel-purple-100" />
                    </div>
                  </div>
                  <div>
                    <div className="mb-1.5 flex items-center justify-between text-sm">
                      <span className="text-text-secondary">탄력도</span>
                      <span className="font-medium text-text-primary">89%</span>
                    </div>
                    <div className="h-2.5 overflow-hidden rounded-full bg-muted">
                      <div className="h-full w-[89%] rounded-full bg-gradient-to-r from-pastel-blue-200 to-pastel-blue-100" />
                    </div>
                  </div>
                </div>

                {/* AI Recommendation */}
                <div className="rounded-xl bg-primary-100/50 p-4">
                  <div className="mb-2 flex items-center gap-2">
                    <Sparkles className="h-4 w-4 text-primary-500" />
                    <span className="text-sm font-medium text-text-primary">
                      AI 추천
                    </span>
                  </div>
                  <p className="mb-3 text-sm text-text-secondary">
                    수분도가 높은 편이지만, 유분 밸런스 개선이 필요합니다.
                    나이아신아마이드 성분의 세럼과 가벼운 젤 타입 보습제를 추천드립니다.
                  </p>
                  <button
                    type="button"
                    className="inline-flex items-center gap-1 text-sm font-medium text-primary-500 hover:text-primary-400"
                  >
                    맞춤 제품 보기
                    <ChevronRight className="h-4 w-4" />
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </section>
  )
}

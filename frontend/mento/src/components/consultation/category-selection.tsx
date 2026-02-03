import { Link } from "react-router-dom"
import { Sparkles, Scissors, Droplets, ArrowRight, CalendarCheck } from "lucide-react"

type ConsultationCategory = "skincare" | "beauty" | "hair" | "general" | null

interface CategorySelectionProps {
  selectedCategory: ConsultationCategory
  onSelect: (category: ConsultationCategory) => void
  onNext: () => void
  canProceed: boolean
}

const categories = [
  {
    id: "skincare" as const,
    label: "스킨 케어",
    description: "피부 타입 분석 및 맞춤 스킨케어 루틴 상담",
    icon: Droplets,
    gradient: "from-pastel-blue-100 to-pastel-blue-200",
    hoverGradient: "hover:from-pastel-blue-200 hover:to-primary-200",
    iconColor: "text-primary-500",
  },
  {
    id: "beauty" as const,
    label: "뷰티",
    description: "퍼스널 컬러 및 메이크업 스타일링 상담",
    icon: Sparkles,
    gradient: "from-pastel-purple-100 to-pastel-purple-200",
    hoverGradient: "hover:from-pastel-purple-200 hover:to-[#D8D4F5]",
    iconColor: "text-[#8B7CF5]",
  },
  {
    id: "hair" as const,
    label: "헤어",
    description: "얼굴형에 맞는 헤어스타일 및 관리법 상담",
    icon: Scissors,
    gradient: "from-pastel-green-100 to-pastel-green-200",
    hoverGradient: "hover:from-pastel-green-200 hover:to-[#9AE6C9]",
    iconColor: "text-[#2ECC71]",
  },
]

export function CategorySelection({
  selectedCategory,
  onSelect,
  onNext,
  canProceed,
}: CategorySelectionProps) {
  return (
    <div className="flex flex-col">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">
            상담 받을 분야를 선택해 주세요
          </h1>
          <p className="mt-2 text-text-secondary">
            전문 상담사가 맞춤형 조언을 제공해 드립니다
          </p>
        </div>
        <Link
          to="/mypage/consultations"
          onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}
          className="flex items-center gap-2 rounded-lg border border-border px-4 py-2 text-sm font-medium text-text-secondary transition-colors hover:bg-muted hover:text-text-primary"
        >
          <CalendarCheck className="h-4 w-4" />
          상담 예약 내역 보러가기
        </Link>
      </div>

      {/* Category Cards */}
      <div className="mt-10 grid grid-cols-1 gap-6 md:grid-cols-3">
        {categories.map((category) => {
          const Icon = category.icon
          const isSelected = selectedCategory === category.id

          return (
            <button
              key={category.id}
              type="button"
              onClick={() => onSelect(category.id)}
              className={`group relative flex flex-col items-center rounded-2xl bg-gradient-to-br p-8 transition-all duration-300 ${category.gradient} ${category.hoverGradient} ${
                isSelected
                  ? "ring-2 ring-primary-500 ring-offset-2 shadow-lg"
                  : "hover:shadow-md"
              }`}
            >
              {/* Selected Indicator */}
              {isSelected && (
                <div className="absolute right-4 top-4 flex h-6 w-6 items-center justify-center rounded-full bg-primary-500">
                  <svg
                    className="h-4 w-4 text-dark-bg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth={3}
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M5 13l4 4L19 7"
                    />
                  </svg>
                </div>
              )}

              {/* Icon */}
              <div
                className={`flex h-20 w-20 items-center justify-center rounded-full bg-background/60 backdrop-blur-sm ${category.iconColor}`}
              >
                <Icon className="h-10 w-10" />
              </div>

              {/* Label */}
              <h3 className="mt-6 text-xl font-bold text-text-primary">
                {category.label}
              </h3>

              {/* Description */}
              <p className="mt-3 text-center text-sm leading-relaxed text-text-secondary">
                {category.description}
              </p>
            </button>
          )
        })}
      </div>

      {/* Next Button */}
      <div className="mt-12 flex justify-end">
        <button
          type="button"
          onClick={onNext}
          disabled={!canProceed}
          className={`flex items-center gap-2 rounded-xl px-8 py-3 text-base font-semibold transition-all ${
            canProceed
              ? "bg-primary-500 text-dark-bg hover:bg-primary-400 shadow-lg shadow-primary-500/30"
              : "cursor-not-allowed bg-muted text-muted-foreground"
          }`}
        >
          다음 단계
          <ArrowRight className="h-5 w-5" />
        </button>
      </div>
    </div>
  )
}

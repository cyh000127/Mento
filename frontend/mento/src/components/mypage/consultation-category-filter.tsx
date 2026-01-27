import type { ConsultationCategory } from "@/types/consultation"

interface ConsultationCategoryFilterProps {
  selectedCategory: ConsultationCategory | "all"
  onCategoryChange: (category: ConsultationCategory | "all") => void
}

const categoryLabels: Record<ConsultationCategory | "all", string> = {
  all: "전체",
  skincare: "스킨케어",
  beauty: "뷰티",
  hair: "헤어",
}

export function ConsultationCategoryFilter({
  selectedCategory,
  onCategoryChange,
}: ConsultationCategoryFilterProps) {
  return (
    <div className="flex items-center gap-3">
      <span className="text-sm font-medium text-foreground">유형</span>
      <div className="flex flex-wrap gap-2">
        {(Object.keys(categoryLabels) as Array<ConsultationCategory | "all">).map((category) => (
          <button
            key={category}
            onClick={() => onCategoryChange(category)}
            className={`rounded-lg px-4 py-2 text-sm font-medium transition-all ${
              selectedCategory === category
                ? "bg-dark-bg text-white"
                : "bg-muted text-muted-foreground hover:bg-muted/80"
            }`}
          >
            {categoryLabels[category]}
          </button>
        ))}
      </div>
    </div>
  )
}

interface ConsultationCategoryFilterProps {
  options: string[]
  selectedCategory: string | "all"
  onCategoryChange: (category: string | "all") => void
}

export function ConsultationCategoryFilter({
  options,
  selectedCategory,
  onCategoryChange,
}: ConsultationCategoryFilterProps) {
  const categories = ["all", ...options]

  return (
    <div className="flex items-center gap-3">
      <span className="text-sm font-medium text-foreground">유형</span>
      <div className="flex flex-wrap gap-2">
        {categories.map((category) => (
          <button
            key={category}
            onClick={() => onCategoryChange(category)}
            className={`rounded-lg px-4 py-2 text-sm font-medium transition-all ${
              selectedCategory === category
                ? "bg-dark-bg text-white"
                : "bg-muted text-muted-foreground hover:bg-muted/80"
            }`}
          >
            {category === "all" ? "전체" : category}
          </button>
        ))}
      </div>
    </div>
  )
}

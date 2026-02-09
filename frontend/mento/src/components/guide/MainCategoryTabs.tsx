interface MainCategoryTabsProps {
  activeCategory: string
  onCategoryChange: (categoryId: string) => void
}

type MainCategory = {
  id: string
  label: string
}

const mainCategories: MainCategory[] = [
  { id: "skinCare", label: "Skin Care" },
  { id: "beauty", label: "Beauty" },
]

export function MainCategoryTabs({
  activeCategory,
  onCategoryChange,
}: MainCategoryTabsProps) {
  return (
    <div className="w-full max-w-md">
      <div className="flex items-center gap-2 p-1.5 bg-muted rounded-xl shadow-sm border border-border">
        {mainCategories.map((category) => {
          const isActive = activeCategory === category.id

          return (
            <button
              key={category.id}
              onClick={() => onCategoryChange(category.id)}
              className={`
                flex-1 px-6 py-3.5 rounded-lg font-semibold text-sm
                transition-all duration-300
                ${
                  isActive
                    ? "bg-primary-500 text-white shadow-md scale-[1.02]"
                    : "text-text-secondary hover:text-text-primary hover:bg-background/50"
                }
              `}
              aria-current={isActive ? "page" : undefined}
            >
              {category.label}
            </button>
          )
        })}
      </div>
    </div>
  )
}

import { Droplets, Sparkles, Scissors } from "lucide-react"

type Category = {
  id: string
  label: string
  icon: React.ComponentType<{ className?: string }>
}

const categories: Category[] = [
  {
    id: "skincare",
    label: "스킨케어",
    icon: Droplets,
  },
  {
    id: "beauty",
    label: "뷰티",
    icon: Sparkles,
  },
  {
    id: "hair",
    label: "헤어",
    icon: Scissors,
  },
]

interface CategorySidebarProps {
  activeCategory: string
  onCategoryChange: (categoryId: string) => void
}

export function CategorySidebar({
  activeCategory,
  onCategoryChange,
}: CategorySidebarProps) {
  return (
    <aside className="hidden lg:block w-64 flex-shrink-0">
      <div className="sticky top-24 space-y-2">
        <h2 className="text-sm font-semibold text-text-secondary uppercase tracking-wide mb-4 px-3">
          카테고리
        </h2>
        <nav className="space-y-1">
          {categories.map((category) => {
            const Icon = category.icon
            const isActive = activeCategory === category.id

            return (
              <button
                key={category.id}
                onClick={() => onCategoryChange(category.id)}
                className={`
                  w-full flex items-center gap-3 px-4 py-3 rounded-lg
                  transition-all duration-200
                  ${
                    isActive
                      ? "bg-primary-100 text-primary-500 font-semibold"
                      : "text-text-primary hover:bg-muted"
                  }
                `}
                aria-current={isActive ? "page" : undefined}
              >
                <Icon
                  className={`h-5 w-5 ${isActive ? "text-primary-500" : "text-text-secondary"}`}
                />
                <span>{category.label}</span>
              </button>
            )
          })}
        </nav>
      </div>
    </aside>
  )
}

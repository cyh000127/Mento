import { useState } from "react"
import { Droplets, Sun, Leaf, Zap } from "lucide-react"

const skinTypes = [
  { id: "all", label: "전체", icon: null },
  { id: "dry", label: "건성", icon: Droplets },
  { id: "oily", label: "지성", icon: Sun },
  { id: "sensitive", label: "민감성", icon: Leaf },
  { id: "combination", label: "복합성", icon: Zap },
]

const categories = [
  { id: "all", label: "전체" },
  { id: "cleanser", label: "클렌저" },
  { id: "toner", label: "토너" },
  { id: "serum", label: "세럼" },
  { id: "moisturizer", label: "보습제" },
  { id: "sunscreen", label: "선케어" },
]

export function SkinTypeFilter() {
  const [selectedSkinType, setSelectedSkinType] = useState("all")
  const [selectedCategory, setSelectedCategory] = useState("all")

  return (
    <section className="border-b border-border bg-background py-6">
      <div className="mx-auto max-w-[1200px] px-6">
        <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
          {/* Skin Type Filter */}
          <div className="flex flex-col gap-3">
            <p className="text-sm font-medium text-text-secondary">피부 타입</p>
            <div className="flex flex-wrap gap-2">
              {skinTypes.map((type) => (
                <button
                  key={type.id}
                  type="button"
                  onClick={() => setSelectedSkinType(type.id)}
                  className={`inline-flex items-center gap-1.5 rounded-full px-4 py-2 text-sm font-medium transition-all ${
                    selectedSkinType === type.id
                      ? "bg-primary-500 text-dark-bg shadow-md shadow-primary-500/25"
                      : "bg-muted text-text-secondary hover:bg-muted/80 hover:text-text-primary"
                  }`}
                >
                  {type.icon && <type.icon className="h-4 w-4" />}
                  {type.label}
                </button>
              ))}
            </div>
          </div>

          {/* Category Filter */}
          <div className="flex flex-col gap-3">
            <p className="text-sm font-medium text-text-secondary">카테고리</p>
            <div className="flex flex-wrap gap-2">
              {categories.map((category) => (
                <button
                  key={category.id}
                  type="button"
                  onClick={() => setSelectedCategory(category.id)}
                  className={`rounded-full px-4 py-2 text-sm font-medium transition-all ${
                    selectedCategory === category.id
                      ? "bg-dark-bg text-white"
                      : "bg-muted text-text-secondary hover:bg-muted/80 hover:text-text-primary"
                  }`}
                >
                  {category.label}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

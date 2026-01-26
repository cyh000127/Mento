import { Link } from "react-router-dom"
import { Droplets, Scissors, Sparkles, Wind, ArrowRight } from "lucide-react"

const categories = [
  {
    id: "skincare",
    title: "스킨케어 기초",
    description: "세안부터 보습까지, 피부 관리의 기본을 배웁니다.",
    icon: Droplets,
    articles: 24,
    gradient: "from-primary-400 to-primary-500",
    bgColor: "bg-primary-100/50",
  },
  {
    id: "haircare",
    title: "헤어케어",
    description: "두피 건강과 헤어 스타일링의 모든 것을 알아봅니다.",
    icon: Scissors,
    articles: 18,
    gradient: "from-pastel-purple-200 to-pastel-purple-100",
    bgColor: "bg-pastel-purple-100/50",
  },
  {
    id: "grooming",
    title: "그루밍 루틴",
    description: "매일 실천할 수 있는 효과적인 루틴을 설계합니다.",
    icon: Sparkles,
    articles: 15,
    gradient: "from-pastel-green-200 to-pastel-green-100",
    bgColor: "bg-pastel-green-100/50",
  },
  {
    id: "fragrance",
    title: "향수 가이드",
    description: "상황별 향수 선택과 올바른 사용법을 익힙니다.",
    icon: Wind,
    articles: 12,
    gradient: "from-pastel-blue-200 to-pastel-blue-100",
    bgColor: "bg-pastel-blue-100/50",
  },
]

export function GuideCategories() {
  return (
    <section className="bg-background py-12 md:py-16">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Section Header */}
        <div className="mb-10">
          <h2 className="text-2xl font-bold text-text-primary">카테고리</h2>
          <p className="mt-2 text-text-secondary">
            관심 있는 분야를 선택하여 학습을 시작하세요.
          </p>
        </div>

        {/* Categories Grid */}
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {categories.map((category) => (
            <Link
              key={category.id}
              to={`/guide/${category.id}`}
              className={`group relative overflow-hidden rounded-2xl ${category.bgColor} p-6 transition-all hover:-translate-y-1 hover:shadow-lg hover:shadow-primary-500/5`}
            >
              {/* Icon */}
              <div
                className={`mb-4 inline-flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br ${category.gradient}`}
              >
                <category.icon className="h-6 w-6 text-dark-bg" />
              </div>

              {/* Content */}
              <h3 className="mb-2 text-lg font-semibold text-text-primary">
                {category.title}
              </h3>
              <p className="mb-4 text-sm leading-relaxed text-text-secondary">
                {category.description}
              </p>

              {/* Article Count & Arrow */}
              <div className="flex items-center justify-between">
                <span className="text-sm text-text-secondary">
                  {category.articles}개 아티클
                </span>
                <ArrowRight className="h-4 w-4 text-text-secondary transition-transform group-hover:translate-x-1 group-hover:text-primary-500" />
              </div>
            </Link>
          ))}
        </div>
      </div>
    </section>
  )
}

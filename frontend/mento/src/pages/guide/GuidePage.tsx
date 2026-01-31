import { useState } from "react"
import { CategorySidebar } from "@/components/guide/CategorySidebar"
import { HowToUseSection } from "@/components/guide/HowToUseSection"
import { BookOpen, Menu } from "lucide-react"

export default function GuidePage() {
  const [activeCategory, setActiveCategory] = useState<string>("skincare")
  const [sidebarOpen, setSidebarOpen] = useState(false)

  return (
    <div className="min-h-screen bg-background">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-br from-pastel-blue-100/50 via-background to-primary-100/30">
        <div className="mx-auto max-w-[1400px] px-6 py-12 md:py-16">
          <div className="flex flex-col items-center text-center">
            {/* Badge */}
            <div className="mb-6 inline-flex items-center gap-2 rounded-full bg-primary-100 px-4 py-1.5">
              <BookOpen className="h-4 w-4 text-primary-500" />
              <span className="text-sm font-medium text-primary-500">
                Usage Guide
              </span>
            </div>

            {/* Title */}
            <h1 className="mb-4 text-balance text-3xl font-bold text-text-primary md:text-4xl lg:text-5xl">
              제품 사용 방법
            </h1>

            {/* Description */}
            <p className="max-w-2xl text-pretty text-base leading-relaxed text-text-secondary md:text-lg">
              Mento의 제품을 올바르게 사용하는 방법을 단계별로 안내합니다.
              <br className="hidden sm:block" />
              얼굴 부위별 맞춤 가이드로 효과적인 사용법을 배워보세요.
            </p>
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="mx-auto max-w-[1400px] px-6 py-12 md:py-16">
        <div className="flex gap-8">
          {/* Mobile Category Toggle */}
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="lg:hidden fixed bottom-6 right-6 z-50 h-14 w-14 rounded-full bg-primary-500 text-white shadow-lg flex items-center justify-center hover:bg-primary-400 transition-colors"
            aria-label="카테고리 메뉴"
          >
            <Menu className="h-6 w-6" />
          </button>

          {/* Mobile Sidebar Overlay */}
          {sidebarOpen && (
            <div
              className="lg:hidden fixed inset-0 bg-dark-bg/50 z-40"
              onClick={() => setSidebarOpen(false)}
            >
              <div
                className="absolute left-0 top-0 bottom-0 w-64 bg-background shadow-xl p-6"
                onClick={(e) => e.stopPropagation()}
              >
                <CategorySidebar
                  activeCategory={activeCategory}
                  onCategoryChange={(category) => {
                    setActiveCategory(category)
                    setSidebarOpen(false)
                  }}
                />
              </div>
            </div>
          )}

          {/* Desktop Sidebar */}
          <CategorySidebar
            activeCategory={activeCategory}
            onCategoryChange={setActiveCategory}
          />

          {/* Main Content Area */}
          <HowToUseSection activeCategory={activeCategory} />
        </div>
      </section>

      {/* CTA Section */}
      <section className="bg-gradient-to-r from-pastel-purple-100 to-pastel-green-100 py-16">
        <div className="mx-auto max-w-[1200px] px-6 text-center">
          <h2 className="text-2xl font-bold text-text-primary mb-4">
            제품 사용에 대해 더 궁금하신가요?
          </h2>
          <p className="text-text-secondary mb-8 max-w-2xl mx-auto">
            전문 멘토와 1:1 상담을 통해 피부 타입에 맞는 맞춤형 제품 사용법을 추천받아보세요.
          </p>
          <a
            href="/consultation"
            className="inline-flex items-center gap-2 rounded-xl bg-dark-bg px-8 py-3.5 font-medium text-primary-500 transition-all hover:bg-dark-bg/90"
          >
            상담 예약하기
          </a>
        </div>
      </section>
    </div>
  )
}

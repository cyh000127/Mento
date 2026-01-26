import { Link } from "react-router-dom"
import { Clock, Eye, BookmarkPlus } from "lucide-react"

const guides = [
  {
    id: 1,
    title: "완벽한 세안의 기술: 남성 피부를 위한 클렌징 가이드",
    category: "스킨케어",
    readTime: "5분",
    views: 12430,
    level: "입문",
    excerpt:
      "올바른 세안법은 스킨케어의 첫 걸음입니다. 이중 세안의 필요성부터 물 온도, 세안제 선택까지 기초부터 알아봅니다.",
  },
  {
    id: 2,
    title: "피부 타입별 토너 선택법",
    category: "스킨케어",
    readTime: "7분",
    views: 8923,
    level: "기초",
    excerpt:
      "건성, 지성, 복합성, 민감성 피부 타입에 따라 토너 선택 기준이 달라집니다. 나에게 맞는 토너를 찾는 방법을 알아봅니다.",
  },
  {
    id: 3,
    title: "남성 헤어 스타일링 완전 정복",
    category: "헤어케어",
    readTime: "10분",
    views: 15672,
    level: "기초",
    excerpt:
      "왁스, 포마드, 젤의 차이점과 얼굴형에 맞는 헤어 스타일 선택법. 드라이 기술까지 모두 다룹니다.",
  },
  {
    id: 4,
    title: "자외선 차단제, 제대로 알고 바르자",
    category: "스킨케어",
    readTime: "6분",
    views: 11245,
    level: "입문",
    excerpt:
      "SPF, PA의 의미부터 올바른 도포량, 재도포 타이밍까지. 선크림에 대한 모든 것을 정리했습니다.",
  },
  {
    id: 5,
    title: "일주일 그루밍 루틴 설계하기",
    category: "그루밍",
    readTime: "8분",
    views: 9876,
    level: "중급",
    excerpt:
      "매일 해야 할 것과 주 1-2회 해야 할 것을 구분하여 효율적인 그루밍 루틴을 만드는 방법을 소개합니다.",
  },
  {
    id: 6,
    title: "첫 향수 구매 가이드",
    category: "향수",
    readTime: "9분",
    views: 7654,
    level: "입문",
    excerpt:
      "향수 구매가 처음이라면? 기본 용어부터 시향 방법, 예산별 추천까지 초보자를 위한 완벽 가이드.",
  },
]

const levelColors: Record<string, string> = {
  입문: "bg-pastel-green-100 text-text-primary",
  기초: "bg-primary-100 text-primary-500",
  중급: "bg-pastel-purple-100 text-text-primary",
  고급: "bg-pastel-blue-100 text-text-primary",
}

export function FeaturedGuides() {
  return (
    <section className="bg-muted/30 py-12 md:py-16">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Section Header */}
        <div className="mb-10 flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold text-text-primary">인기 가이드</h2>
            <p className="mt-2 text-text-secondary">
              가장 많이 읽힌 가이드를 확인해보세요.
            </p>
          </div>
          <Link
            to="/guide/all"
            className="hidden text-sm font-medium text-primary-500 hover:underline md:inline-flex"
          >
            전체 보기
          </Link>
        </div>

        {/* Guides Grid */}
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {guides.map((guide) => (
            <article
              key={guide.id}
              className="group overflow-hidden rounded-2xl border border-border bg-background shadow-sm transition-all hover:-translate-y-1 hover:shadow-lg hover:shadow-primary-500/5"
            >
              {/* Category Header */}
              <div className="flex items-center justify-between border-b border-border px-5 py-3">
                <span className="text-xs font-medium text-primary-500">
                  {guide.category}
                </span>
                <span
                  className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${levelColors[guide.level]}`}
                >
                  {guide.level}
                </span>
              </div>

              {/* Content */}
              <div className="p-5">
                <h3 className="mb-3 line-clamp-2 text-lg font-semibold text-text-primary group-hover:text-primary-500">
                  <Link to={`/guide/article/${guide.id}`}>{guide.title}</Link>
                </h3>
                <p className="mb-4 line-clamp-2 text-sm leading-relaxed text-text-secondary">
                  {guide.excerpt}
                </p>

                {/* Meta */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4 text-xs text-text-secondary">
                    <span className="flex items-center gap-1">
                      <Clock className="h-3.5 w-3.5" />
                      {guide.readTime}
                    </span>
                    <span className="flex items-center gap-1">
                      <Eye className="h-3.5 w-3.5" />
                      {guide.views.toLocaleString()}
                    </span>
                  </div>
                  <button
                    type="button"
                    className="rounded-lg p-1.5 text-text-secondary transition-colors hover:bg-muted hover:text-primary-500"
                    aria-label="북마크"
                  >
                    <BookmarkPlus className="h-4 w-4" />
                  </button>
                </div>
              </div>
            </article>
          ))}
        </div>

        {/* Mobile Show All */}
        <div className="mt-8 text-center md:hidden">
          <Link
            to="/guide/all"
            className="inline-flex items-center gap-2 text-sm font-medium text-primary-500"
          >
            전체 보기
          </Link>
        </div>
      </div>
    </section>
  )
}

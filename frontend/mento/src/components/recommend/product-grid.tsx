import { useState } from "react"
import { Star, Heart, Plus, Check } from "lucide-react"

const products = [
  {
    id: 1,
    name: "퓨어 클렌징 폼",
    brand: "CLEANLAB",
    category: "클렌저",
    skinType: "지성",
    price: 28000,
    rating: 4.8,
    reviews: 1243,
    matchScore: 95,
    tags: ["모공케어", "피지조절"],
    image: "/placeholder.svg?height=200&width=200",
  },
  {
    id: 2,
    name: "하이드라 토너 에센스",
    brand: "AQUA DERMA",
    category: "토너",
    skinType: "건성",
    price: 35000,
    rating: 4.9,
    reviews: 892,
    matchScore: 92,
    tags: ["수분공급", "진정효과"],
    image: "/placeholder.svg?height=200&width=200",
  },
  {
    id: 3,
    name: "비타민 C 세럼",
    brand: "VITA GLOW",
    category: "세럼",
    skinType: "복합성",
    price: 42000,
    rating: 4.7,
    reviews: 2156,
    matchScore: 88,
    tags: ["브라이트닝", "안티에이징"],
    image: "/placeholder.svg?height=200&width=200",
  },
  {
    id: 4,
    name: "세라마이드 모이스처라이저",
    brand: "BARRIER PRO",
    category: "보습제",
    skinType: "민감성",
    price: 38000,
    rating: 4.9,
    reviews: 1567,
    matchScore: 97,
    tags: ["장벽강화", "저자극"],
    image: "/placeholder.svg?height=200&width=200",
  },
  {
    id: 5,
    name: "데일리 선스크린 SPF50+",
    brand: "SUN SAFE",
    category: "선케어",
    skinType: "전체",
    price: 25000,
    rating: 4.6,
    reviews: 3421,
    matchScore: 90,
    tags: ["자외선차단", "무기자차"],
    image: "/placeholder.svg?height=200&width=200",
  },
  {
    id: 6,
    name: "AHA/BHA 클리어 토너",
    brand: "ACID CARE",
    category: "토너",
    skinType: "지성",
    price: 32000,
    rating: 4.5,
    reviews: 789,
    matchScore: 85,
    tags: ["각질케어", "모공관리"],
    image: "/placeholder.svg?height=200&width=200",
  },
]

export function ProductGrid() {
  const [wishlist, setWishlist] = useState<number[]>([])
  const [addedToInventory, setAddedToInventory] = useState<number[]>([])

  const toggleWishlist = (id: number) => {
    setWishlist((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    )
  }

  const toggleInventory = (id: number) => {
    setAddedToInventory((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    )
  }

  return (
    <section className="bg-background py-12 md:py-16">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Results Header */}
        <div className="mb-8 flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-text-primary">추천 제품</h2>
            <p className="text-sm text-text-secondary">
              총 {products.length}개의 제품이 있습니다
            </p>
          </div>
          <select className="rounded-lg border border-border bg-background px-4 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-primary-500">
            <option>추천순</option>
            <option>인기순</option>
            <option>가격 낮은순</option>
            <option>가격 높은순</option>
            <option>평점순</option>
          </select>
        </div>

        {/* Product Grid */}
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {products.map((product) => (
            <div
              key={product.id}
              className="group overflow-hidden rounded-2xl border border-border bg-background shadow-sm transition-all hover:-translate-y-1 hover:shadow-lg hover:shadow-primary-500/5"
            >
              {/* Image Area */}
              <div className="relative aspect-square bg-muted/30">
                {/* Match Score Badge */}
                <div className="absolute left-3 top-3 rounded-full bg-primary-500 px-2.5 py-1 text-xs font-bold text-dark-bg">
                  {product.matchScore}% 매칭
                </div>

                {/* Wishlist Button */}
                <button
                  type="button"
                  onClick={() => toggleWishlist(product.id)}
                  className={`absolute right-3 top-3 flex h-9 w-9 items-center justify-center rounded-full transition-all ${
                    wishlist.includes(product.id)
                      ? "bg-red-500 text-white"
                      : "bg-white/80 text-text-secondary hover:bg-white hover:text-red-500"
                  }`}
                  aria-label="위시리스트에 추가"
                >
                  <Heart
                    className={`h-5 w-5 ${wishlist.includes(product.id) ? "fill-current" : ""}`}
                  />
                </button>

                {/* Product Image Placeholder */}
                <div className="flex h-full items-center justify-center">
                  <div className="flex h-32 w-32 items-center justify-center rounded-2xl bg-gradient-to-br from-primary-200 to-primary-100">
                    <span className="text-3xl font-bold text-primary-500">
                      {product.brand.charAt(0)}
                    </span>
                  </div>
                </div>
              </div>

              {/* Content */}
              <div className="p-4">
                {/* Brand & Category */}
                <div className="mb-2 flex items-center gap-2">
                  <span className="text-xs font-medium text-primary-500">
                    {product.brand}
                  </span>
                  <span className="text-xs text-text-secondary">|</span>
                  <span className="text-xs text-text-secondary">
                    {product.category}
                  </span>
                </div>

                {/* Name */}
                <h3 className="mb-2 font-semibold text-text-primary">
                  {product.name}
                </h3>

                {/* Tags */}
                <div className="mb-3 flex flex-wrap gap-1.5">
                  {product.tags.map((tag) => (
                    <span
                      key={tag}
                      className="rounded-full bg-muted px-2 py-0.5 text-xs text-text-secondary"
                    >
                      {tag}
                    </span>
                  ))}
                </div>

                {/* Rating */}
                <div className="mb-3 flex items-center gap-2">
                  <div className="flex items-center gap-1">
                    <Star className="h-4 w-4 fill-amber-400 text-amber-400" />
                    <span className="text-sm font-medium text-text-primary">
                      {product.rating}
                    </span>
                  </div>
                  <span className="text-xs text-text-secondary">
                    ({product.reviews.toLocaleString()}개 리뷰)
                  </span>
                </div>

                {/* Price & Add Button */}
                <div className="flex items-center justify-between">
                  <p className="text-lg font-bold text-text-primary">
                    {product.price.toLocaleString()}원
                  </p>
                  <button
                    type="button"
                    onClick={() => toggleInventory(product.id)}
                    className={`flex items-center gap-1.5 rounded-lg px-3 py-2 text-sm font-medium transition-all ${
                      addedToInventory.includes(product.id)
                        ? "bg-pastel-green-100 text-text-primary"
                        : "bg-primary-500 text-dark-bg hover:bg-primary-400"
                    }`}
                  >
                    {addedToInventory.includes(product.id) ? (
                      <>
                        <Check className="h-4 w-4" />
                        추가됨
                      </>
                    ) : (
                      <>
                        <Plus className="h-4 w-4" />
                        인벤토리
                      </>
                    )}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Load More */}
        <div className="mt-12 text-center">
          <button
            type="button"
            className="rounded-xl border border-border bg-background px-8 py-3 font-medium text-text-primary transition-all hover:bg-muted"
          >
            더 보기
          </button>
        </div>
      </div>
    </section>
  )
}

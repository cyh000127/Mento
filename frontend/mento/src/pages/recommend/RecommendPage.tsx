import { RecommendHero } from "@/components/recommend/recommend-hero"
import { SkinTypeFilter } from "@/components/recommend/skin-type-filter"
import { ProductGrid } from "@/components/recommend/product-grid"
import { RecommendCta } from "@/components/recommend/recommend-cta"

export default function RecommendPage() {
  return (
    <>
      <RecommendHero />
      <SkinTypeFilter />
      <ProductGrid />
      <RecommendCta />
    </>
  )
}

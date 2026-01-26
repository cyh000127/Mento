import { GuideHero } from "@/components/guide/guide-hero"
import { GuideCategories } from "@/components/guide/guide-categories"
import { FeaturedGuides } from "@/components/guide/featured-guides"
import { GuideCta } from "@/components/guide/guide-cta"

export default function GuidePage() {
  return (
    <>
      <GuideHero />
      <GuideCategories />
      <FeaturedGuides />
      <GuideCta />
    </>
  )
}

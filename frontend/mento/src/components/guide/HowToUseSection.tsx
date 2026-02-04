import { useNavigate } from "react-router-dom"
import { ProductTypeGrid } from "./ProductTypeGrid"
import { PRODUCT_CATEGORIES } from "@/constants/guide.ts"

interface HowToUseSectionProps {
  activeCategory: string
}

export function HowToUseSection({ activeCategory }: HowToUseSectionProps) {
  const navigate = useNavigate()

  // Get products for current category
  const products = PRODUCT_CATEGORIES[activeCategory] || []

  // Navigate to detail page when product is selected
  const handleProductSelect = (productType: string) => {
    navigate(`/guide/${activeCategory}/${productType}`)
  }

  return (
    <div className="flex-1">
      {/* Product Type Selection */}
      <section className="mb-12">
        <h2 className="text-2xl font-bold text-text-primary mb-6">
          카테고리
        </h2>
        <ProductTypeGrid
          productTypes={products}
          selectedType=""
          onTypeSelect={handleProductSelect}
        />
      </section>
    </div>
  )
}

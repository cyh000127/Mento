import { useNavigate } from "react-router-dom"
import { ProductTypeGrid } from "./ProductTypeGrid"

// Product types per category (포장 형태)
const productTypes: Record<string, string[]> = {
  skincare: ["튜브", "펌프", "스프레이", "스틱", "드로퍼", "자"],
  beauty: ["스틱", "자", "패치/시트", "펌프"],
  hair: ["스프레이", "펌프", "폼 디스펜서", "롤온"],
}

interface HowToUseSectionProps {
  activeCategory: string
}

export function HowToUseSection({ activeCategory }: HowToUseSectionProps) {
  const navigate = useNavigate()

  // Get products for current category
  const products = productTypes[activeCategory] || []

  // Navigate to detail page when product is selected
  const handleProductSelect = (productType: string) => {
    navigate(`/guide/${activeCategory}/${productType}`)
  }

  return (
    <div className="flex-1">
      {/* Product Type Selection */}
      <section className="mb-12">
        <h2 className="text-2xl font-bold text-text-primary mb-6">
          포장 형태 선택
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

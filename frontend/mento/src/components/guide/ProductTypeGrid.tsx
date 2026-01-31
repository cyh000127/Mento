import { ProductTypeCard } from "./ProductTypeCard"

interface ProductTypeGridProps {
  productTypes: string[]
  selectedType: string
  onTypeSelect: (type: string) => void
}

export function ProductTypeGrid({
  productTypes,
  selectedType,
  onTypeSelect,
}: ProductTypeGridProps) {
  if (productTypes.length === 0) {
    return (
      <div className="text-center py-12 text-text-secondary">
        이 카테고리에는 제품이 없습니다.
      </div>
    )
  }

  return (
    <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
      {productTypes.map((type) => (
        <ProductTypeCard
          key={type}
          id={type}
          label={type}
          isSelected={selectedType === type}
          onClick={() => onTypeSelect(type)}
        />
      ))}
    </div>
  )
}

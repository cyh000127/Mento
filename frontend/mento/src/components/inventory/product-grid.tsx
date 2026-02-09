import type { Product } from "@/types/inventory"

interface ProductGridProps {
  products: Product[]
  selectedProductId: string | undefined
  onProductSelect: (product: Product) => void
}

export function ProductGrid({
  products,
  selectedProductId,
  onProductSelect,
}: ProductGridProps) {
  // 최소 20개의 그리드 슬롯 표시 (와이어프레임 기준)
  const minGridSlots = 20
  const emptySlots = Math.max(0, minGridSlots - products.length)

  return (
    <div className="rounded-lg border border-border bg-white p-4 shadow-sm">
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
        {/* 실제 제품들 */}
        {products.map((product) => (
        <button
          key={product.id}
          onClick={() => onProductSelect(product)}
          className={`group relative aspect-square overflow-hidden rounded-lg border-2 transition-all hover:shadow-md ${
            selectedProductId === product.id
              ? "border-primary-500 shadow-sm"
              : "border-transparent hover:border-primary-400/50"
          }`}
        >
          <img
            src={product.image}
            alt={product.name}
            className="h-full w-full object-cover transition-transform group-hover:scale-105"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent opacity-0 transition-opacity group-hover:opacity-100" />
        </button>
      ))}
      
        {/* 빈 그리드 슬롯들 */}
        {Array.from({ length: emptySlots }).map((_, index) => (
          <div
            key={`empty-${index}`}
            className="aspect-square rounded-lg bg-muted/30"
          />
        ))}
      </div>

    </div>
  )
}

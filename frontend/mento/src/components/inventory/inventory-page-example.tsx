import { useState } from "react"
import { InventoryFilters } from "./inventory-filters"
import { InventoryRegisterModal } from "./inventory-register-modal"
import type { Product, ProductCategory, ProductStatus, SortOption } from "@/types/inventory"

/**
 * 재고 관리 페이지 예제
 * 
 * 이 컴포넌트는 InventoryRegisterModal의 사용 예제입니다.
 * 실제 페이지에서는 이 패턴을 참고하여 구현하세요.
 */
export function InventoryPageExample() {
  // 필터 상태
  const [searchQuery, setSearchQuery] = useState("")
  const [selectedCategory, setSelectedCategory] = useState<ProductCategory | "all">("all")
  const [sortOption, setSortOption] = useState<SortOption>("recent")
  const [selectedStatus, setSelectedStatus] = useState<ProductStatus | "all">("all")

  // 모달 상태
  const [registerModalOpen, setRegisterModalOpen] = useState(false)

  // 제품 등록 핸들러
  const handleProductsAdded = (selectedProducts: Product[]) => {
    console.log("선택된 제품들:", selectedProducts)
    // 여기서 실제 API 호출을 하거나 상태를 업데이트합니다
    // 예: mutate() 또는 refetch()
    
    // 성공 토스트 메시지 표시 (옵션)
    // toast.success(`${selectedProducts.length}개의 제품이 추가되었습니다`)
  }

  return (
    <div className="container mx-auto p-6">
      <h1 className="mb-6 text-2xl font-bold text-text-primary">재고 관리</h1>

      {/* 필터 섹션 */}
      <InventoryFilters
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        selectedCategory={selectedCategory}
        onCategoryChange={setSelectedCategory}
        sortOption={sortOption}
        onSortChange={setSortOption}
        selectedStatus={selectedStatus}
        onStatusChange={setSelectedStatus}
        onAddProduct={() => setRegisterModalOpen(true)}
      />

      {/* 제품 그리드 (여기서는 예제이므로 생략) */}
      <div className="mt-6">
        <p className="text-text-secondary">여기에 제품 그리드가 표시됩니다</p>
      </div>

      {/* 재고 등록 모달 */}
      <InventoryRegisterModal
        open={registerModalOpen}
        onOpenChange={setRegisterModalOpen}
        onConfirm={handleProductsAdded}
      />
    </div>
  )
}

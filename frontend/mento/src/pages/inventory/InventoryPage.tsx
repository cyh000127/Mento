import { useState, useEffect, useCallback } from "react"
import { ProductGrid } from "@/components/inventory/product-grid"
import { ProductDetail } from "@/components/inventory/product-detail"
import { InventoryFilters } from "@/components/inventory/inventory-filters"
import { InventoryRegisterModal } from "@/components/inventory/inventory-register-modal"
import type { Product, ProductCategory, ProductStatus, SortOption } from "@/types/inventory"
import {
  getInventoryItems,
  mapApiItemToProduct,
  mapUiStatusToApiStatus,
  mapUiSortToApiSort,
} from "@/api/inventory"

export default function InventoryPage() {
  const [products, setProducts] = useState<Product[]>([])
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null)
  const [loading, setLoading] = useState(false)
  const [currentPage, setCurrentPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [hasNext, setHasNext] = useState(false)
  const [searchQuery, setSearchQuery] = useState("")
  const [selectedCategory, setSelectedCategory] = useState<ProductCategory | "all">("all")
  const [sortOption, setSortOption] = useState<SortOption>("recent")
  const [selectedStatus, setSelectedStatus] = useState<ProductStatus | "all">("all")
  const [favoriteFilter, setFavoriteFilter] = useState<boolean | undefined>(undefined)
  const [registerModalOpen, setRegisterModalOpen] = useState(false)

  // API 데이터 가져오기
  const fetchInventory = useCallback(async () => {
    setLoading(true)
    try {
      const filters: any = {
        page: currentPage,
        size: 20,
      }

      // 카테고리 필터 적용
      if (selectedCategory !== "all") {
        filters.category = selectedCategory.toUpperCase()
      }

      // 상태 필터 적용
      if (selectedStatus !== "all") {
        filters.status = mapUiStatusToApiStatus(selectedStatus)
      }

      // 즐겨찾기 필터 적용
      if (favoriteFilter !== undefined) {
        filters.isFavorite = favoriteFilter
      }

      // 정렬 옵션 적용
      filters.sort = mapUiSortToApiSort(sortOption)

      const response = await getInventoryItems(filters)

      const mappedProducts = response.content.map(mapApiItemToProduct)
      setProducts(mappedProducts)
      setTotalPages(response.totalPages)
      setHasNext(response.hasNext)

      // 선택된 제품이 없으면 첫 번째 제품 선택
      if (mappedProducts.length > 0) {
        setSelectedProduct((prev) => {
          if (!prev) return mappedProducts[0]
          // 현재 선택된 제품이 새 목록에 있는지 확인
          const stillExists = mappedProducts.find((p) => p.id === prev.id)
          return stillExists || mappedProducts[0]
        })
      } else {
        setSelectedProduct(null)
      }
    } catch (error) {
      console.error("Failed to fetch inventory:", error)
      setProducts([])
      setSelectedProduct(null)
    } finally {
      setLoading(false)
    }
  }, [currentPage, selectedCategory, selectedStatus, favoriteFilter, sortOption])

  // 초기 로드 및 필터/정렬 변경 시 데이터 재조회
  useEffect(() => {
    fetchInventory()
  }, [fetchInventory])

  // 필터가 변경되면 첫 페이지로 이동
  useEffect(() => {
    setCurrentPage(0)
  }, [selectedCategory, selectedStatus, favoriteFilter, sortOption])

  // 검색 필터 (클라이언트 사이드)
  const filteredProducts = products.filter((product) => {
    if (searchQuery && !product.name.toLowerCase().includes(searchQuery.toLowerCase())) {
      return false
    }
    return true
  })

  const handleProductSelect = (product: Product) => {
    setSelectedProduct(product)
  }

  const handleToggleFavorite = async (productId: string) => {
    // TODO: 즐겨찾기 토글 API 구현 예정
    console.log("Toggle favorite:", productId)
    // API 호출 후 데이터 재조회
    await fetchInventory()
  }

  const handleDelete = async (productId: string) => {
    // TODO: 제품 삭제 API 구현 예정
    console.log("Delete product:", productId)
    // API 호출 후 데이터 재조회
    await fetchInventory()
  }

  const handleAddProduct = () => {
    setRegisterModalOpen(true)
  }

  const handleProductsAdded = async (selectedProducts: Product[]) => {
    // TODO: 제품 추가 API 구현 예정
    console.log("Products added to inventory:", selectedProducts)
    // API 호출 후 데이터 재조회
    await fetchInventory()
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="mx-auto max-w-[1400px] px-6 py-8">
        {/* Main Content */}
        <div className="grid gap-6 lg:grid-cols-3">
          {/* Left Section - Product List */}
          <div className="lg:col-span-2">
            <InventoryFilters
              searchQuery={searchQuery}
              onSearchChange={setSearchQuery}
              selectedCategory={selectedCategory}
              onCategoryChange={setSelectedCategory}
              sortOption={sortOption}
              onSortChange={setSortOption}
              selectedStatus={selectedStatus}
              onStatusChange={setSelectedStatus}
              onAddProduct={handleAddProduct}
            />

            <ProductGrid
              products={filteredProducts}
              selectedProductId={selectedProduct?.id}
              onProductSelect={handleProductSelect}
            />
          </div>

          {/* Right Section - Product Detail */}
          <div className="lg:col-span-1">
            {selectedProduct && (
              <ProductDetail
                product={selectedProduct}
                onToggleFavorite={handleToggleFavorite}
                onDelete={handleDelete}
              />
            )}
          </div>
        </div>
      </div>

      {/* Inventory Register Modal */}
      <InventoryRegisterModal
        open={registerModalOpen}
        onOpenChange={setRegisterModalOpen}
        onConfirm={handleProductsAdded}
      />
    </div>
  )
}

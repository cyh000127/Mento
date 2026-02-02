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
  addInventoryItem,
  deleteInventoryItem,
} from "@/api/inventoryApi"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { useToast } from "@/hooks/use-toast"

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
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [productToDelete, setProductToDelete] = useState<Product | null>(null)
  const { toast } = useToast()

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

  const handleDelete = (productId: string) => {
    const product = products.find((p) => p.id === productId)
    if (product) {
      setProductToDelete(product)
      setDeleteDialogOpen(true)
    }
  }

  const confirmDelete = async () => {
    if (!productToDelete) return

    // Optimistic UI를 위한 이전 상태 저장
    const previousProducts = [...products]
    const previousSelectedProduct = selectedProduct

    try {
      // UI에서 즉시 제거
      setProducts((prev) => prev.filter((p) => p.id !== productToDelete.id))
      
      // 선택된 제품이 삭제된 경우 다른 제품 선택
      if (selectedProduct?.id === productToDelete.id) {
        const remainingProducts = products.filter((p) => p.id !== productToDelete.id)
        setSelectedProduct(remainingProducts.length > 0 ? remainingProducts[0] : null)
      }

      // API 호출
      await deleteInventoryItem(productToDelete.id)

      // 성공 알림
      toast({
        title: "삭제 완료",
        description: "아이템이 인벤토리에서 제거되었습니다.",
        variant: "default",
      })

      setDeleteDialogOpen(false)
      setProductToDelete(null)
    } catch (error: any) {
      // 실패 시 원래 상태로 복원
      setProducts(previousProducts)
      setSelectedProduct(previousSelectedProduct)

      // 에러 알림
      const errorMessage = error.response?.data?.message || "아이템 삭제 중 오류가 발생했습니다."
      toast({
        title: "삭제 실패",
        description: errorMessage,
        variant: "destructive",
      })
    }
  }

  const handleAddProduct = () => {
    setRegisterModalOpen(true)
  }

  const handleProductsAdded = async (selectedProducts: Product[]) => {
    try {
      // 현재 인벤토리 목록을 최신 상태로 가져오기
      await fetchInventory()
      
      // 중복 체크: 현재 인벤토리에 이미 있는 상품 찾기
      const duplicateProducts: Product[] = []
      const productsToAdd: Product[] = []
      
      selectedProducts.forEach((selectedProduct) => {
        // products 배열에서 같은 이름의 상품이 있는지 확인
        const isDuplicate = products.some(
          (existingProduct) => existingProduct.name === selectedProduct.name
        )
        
        if (isDuplicate) {
          duplicateProducts.push(selectedProduct)
        } else {
          productsToAdd.push(selectedProduct)
        }
      })

      // 중복된 상품이 있으면 경고 메시지 표시
      if (duplicateProducts.length > 0) {
        const duplicateNames = duplicateProducts.map(p => `"${p.name}"`).join(", ")
        
        if (productsToAdd.length > 0) {
          const confirmMessage = `다음 상품은 이미 인벤토리에 존재합니다:\n${duplicateNames}\n\n나머지 ${productsToAdd.length}개 상품을 추가하시겠습니까?`
          
          if (!confirm(confirmMessage)) {
            return // 사용자가 취소한 경우
          }
        } else {
          alert(`선택한 모든 상품이 이미 인벤토리에 존재합니다:\n${duplicateNames}`)
          return
        }
      }

      // 중복되지 않은 상품만 추가
      if (productsToAdd.length === 0) {
        return
      }

      const results = await Promise.allSettled(
        productsToAdd.map((product) =>
          addInventoryItem({
            productId: parseInt(product.id),
          }).then(() => ({ product, success: true }))
            .catch((error) => ({ product, success: false, error }))
        )
      )

      // 성공/실패 결과 분류
      const successResults = results
        .filter((r) => r.status === "fulfilled" && r.value.success)
        .map((r) => (r as PromiseFulfilledResult<any>).value.product)
      
      const failedResults = results
        .filter((r) => r.status === "fulfilled" && !r.value.success)
        .map((r) => (r as PromiseFulfilledResult<any>).value)

      // 에러 메시지 생성
      const errorMessages: string[] = []
      
      if (failedResults.length > 0) {
        const failedNames = failedResults.map(({ product }) => `"${product.name}"`).join(", ")
        errorMessages.push(`다음 상품 추가 중 오류가 발생했습니다:\n${failedNames}`)
      }

      // 결과 알림
      if (successResults.length > 0 && (duplicateProducts.length > 0 || errorMessages.length > 0)) {
        const messages = [`${successResults.length}개의 상품이 추가되었습니다.`]
        if (duplicateProducts.length > 0) {
          const duplicateNames = duplicateProducts.map(p => `"${p.name}"`).join(", ")
          messages.push(`${duplicateProducts.length}개의 중복 상품은 제외되었습니다:\n${duplicateNames}`)
        }
        if (errorMessages.length > 0) {
          messages.push(...errorMessages)
        }
        alert(messages.join("\n\n"))
      } else if (successResults.length > 0) {
        alert(`${successResults.length}개의 상품이 인벤토리에 추가되었습니다.`)
      } else if (errorMessages.length > 0) {
        alert(errorMessages.join("\n\n"))
      }

      // API 호출 후 데이터 재조회
      await fetchInventory()
    } catch (error) {
      console.error("상품 추가 중 예상치 못한 오류:", error)
      alert("상품 추가 중 오류가 발생했습니다.")
    }
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

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>아이템 삭제</AlertDialogTitle>
            <AlertDialogDescription>
              이 아이템을 인벤토리에서 제거하시겠습니까?
              {productToDelete && (
                <span className="mt-2 block font-medium text-foreground">
                  {productToDelete.name}
                </span>
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setProductToDelete(null)}>
              취소
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmDelete}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              삭제
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}

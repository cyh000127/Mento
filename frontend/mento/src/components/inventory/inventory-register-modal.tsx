import { useState } from "react"
import { Search, X } from "lucide-react"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import type { Product, ProductCategory } from "@/types/inventory"

interface InventoryRegisterModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onConfirm: (selectedProducts: Product[]) => void
}

// 임시 데이터 (실제로는 API에서 가져올 데이터)
const MOCK_PRODUCTS: Product[] = [
  {
    id: "1",
    name: "수분 크림",
    brand: "라운드랩",
    category: "skin",
    image: "https://via.placeholder.com/150",
    purchaseDate: "2024-01-15",
    expirationDate: "2025-01-15",
    repurchaseCount: 0,
    status: "in-use",
    purchaseLink: "",
    isFavorite: false,
  },
  {
    id: "2",
    name: "토너 패드",
    brand: "메디힐",
    category: "skin",
    image: "https://via.placeholder.com/150",
    purchaseDate: "2024-01-20",
    expirationDate: "2025-01-20",
    repurchaseCount: 0,
    status: "in-use",
    purchaseLink: "",
    isFavorite: false,
  },
  {
    id: "3",
    name: "립밤",
    brand: "니베아",
    category: "beauty",
    image: "https://via.placeholder.com/150",
    purchaseDate: "2024-02-01",
    expirationDate: "2025-02-01",
    repurchaseCount: 0,
    status: "in-use",
    purchaseLink: "",
    isFavorite: false,
  },
  {
    id: "4",
    name: "헤어 왁스",
    brand: "가츠비",
    category: "hair",
    image: "https://via.placeholder.com/150",
    purchaseDate: "2024-02-10",
    expirationDate: "2025-02-10",
    repurchaseCount: 0,
    status: "in-use",
    purchaseLink: "",
    isFavorite: false,
  },
]

const categoryLabels: Record<ProductCategory, string> = {
  skin: "스킨",
  beauty: "뷰티",
  hair: "헤어",
}

interface ProductSearchCardProps {
  product: Product
  isSelected: boolean
  onToggleSelect: (productId: string) => void
}

function ProductSearchCard({ product, isSelected, onToggleSelect }: ProductSearchCardProps) {
  return (
    <div
      className={`group relative rounded-lg border-2 bg-white p-3 transition-all hover:shadow-md ${
        isSelected
          ? "border-primary-500 bg-primary-100/30"
          : "border-border hover:border-primary-400/50"
      }`}
    >
      <div className="flex gap-3">
        {/* 체크박스 */}
        <div className="flex items-start pt-1">
          <Checkbox
            checked={isSelected}
            onCheckedChange={() => onToggleSelect(product.id)}
            className="data-[state=checked]:bg-primary-500 data-[state=checked]:border-primary-500"
          />
        </div>

        {/* 제품 이미지 */}
        <div className="h-16 w-16 flex-shrink-0 overflow-hidden rounded-md">
          <img
            src={product.image}
            alt={product.name}
            className="h-full w-full object-cover"
          />
        </div>

        {/* 제품 정보 */}
        <div className="flex-1 min-w-0">
          <div className="mb-1">
            <span className="inline-block rounded-full bg-pastel-blue-200 px-2 py-0.5 text-xs font-medium text-text-primary">
              {categoryLabels[product.category]}
            </span>
          </div>
          <h4 className="mb-0.5 text-sm font-semibold text-text-primary truncate">
            {product.name}
          </h4>
          <p className="text-xs text-text-secondary">{product.brand}</p>
        </div>
      </div>
    </div>
  )
}

interface SelectedProductItemProps {
  product: Product
  onRemove: (productId: string) => void
}

function SelectedProductItem({ product, onRemove }: SelectedProductItemProps) {
  return (
    <div className="flex items-center gap-3 rounded-lg border border-border bg-white p-2 transition-all hover:shadow-sm">
      {/* 제품 이미지 */}
      <div className="h-12 w-12 flex-shrink-0 overflow-hidden rounded">
        <img
          src={product.image}
          alt={product.name}
          className="h-full w-full object-cover"
        />
      </div>

      {/* 제품 정보 */}
      <div className="flex-1 min-w-0">
        <h4 className="text-sm font-medium text-text-primary truncate">
          {product.name}
        </h4>
        <p className="text-xs text-text-secondary">{product.brand}</p>
      </div>

      {/* 제거 버튼 */}
      <button
        onClick={() => onRemove(product.id)}
        className="flex-shrink-0 rounded-full p-1 hover:bg-muted transition-colors"
        aria-label="제거"
      >
        <X className="h-4 w-4 text-text-secondary" />
      </button>
    </div>
  )
}

interface ConfirmAddModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  selectedProducts: Product[]
  onConfirm: () => void
}

function ConfirmAddModal({
  open,
  onOpenChange,
  selectedProducts,
  onConfirm,
}: ConfirmAddModalProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>재고 추가</DialogTitle>
        </DialogHeader>

        <div className="space-y-4">
          {/* 선택된 제품 요약 */}
          <div className="space-y-2 max-h-60 overflow-y-auto">
            {selectedProducts.map((product) => (
              <div
                key={product.id}
                className="flex items-center gap-3 rounded-lg border border-border bg-muted/30 p-3"
              >
                <div className="h-12 w-12 flex-shrink-0 overflow-hidden rounded">
                  <img
                    src={product.image}
                    alt={product.name}
                    className="h-full w-full object-cover"
                  />
                </div>
                <div className="flex-1 min-w-0">
                  <h4 className="text-sm font-medium text-text-primary truncate">
                    {product.name}
                  </h4>
                  <p className="text-xs text-text-secondary">{product.brand}</p>
                </div>
              </div>
            ))}
          </div>

          {/* 확인 메시지 */}
          <p className="text-center text-sm text-text-secondary">
            총 <span className="font-semibold text-primary-500">{selectedProducts.length}개</span>의 제품을 추가하시겠습니까?
          </p>

          {/* 액션 버튼 */}
          <div className="flex gap-3">
            <Button
              variant="outline"
              onClick={() => onOpenChange(false)}
              className="flex-1"
            >
              취소
            </Button>
            <Button
              onClick={onConfirm}
              className="flex-1 bg-primary-500 text-dark-bg hover:bg-primary-400"
            >
              추가
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}

export function InventoryRegisterModal({
  open,
  onOpenChange,
  onConfirm,
}: InventoryRegisterModalProps) {
  const [searchQuery, setSearchQuery] = useState("")
  const [selectedProducts, setSelectedProducts] = useState<Product[]>([])
  const [showConfirmModal, setShowConfirmModal] = useState(false)
  const [currentPage, setCurrentPage] = useState(1)

  // 검색 필터링
  const filteredProducts = MOCK_PRODUCTS.filter((product) => {
    const searchLower = searchQuery.toLowerCase()
    return (
      product.name.toLowerCase().includes(searchLower) ||
      product.brand.toLowerCase().includes(searchLower)
    )
  })

  // 페이지네이션 (한 페이지에 6개씩)
  const itemsPerPage = 6
  const totalPages = Math.ceil(filteredProducts.length / itemsPerPage)
  const paginatedProducts = filteredProducts.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  )

  const handleToggleSelect = (productId: string) => {
    const product = MOCK_PRODUCTS.find((p) => p.id === productId)
    if (!product) return

    setSelectedProducts((prev) => {
      const isAlreadySelected = prev.some((p) => p.id === productId)
      if (isAlreadySelected) {
        return prev.filter((p) => p.id !== productId)
      } else {
        return [...prev, product]
      }
    })
  }

  const handleRemoveSelected = (productId: string) => {
    setSelectedProducts((prev) => prev.filter((p) => p.id !== productId))
  }

  const handleConfirmSelection = () => {
    if (selectedProducts.length === 0) return
    setShowConfirmModal(true)
  }

  const handleFinalConfirm = () => {
    onConfirm(selectedProducts)
    setShowConfirmModal(false)
    setSelectedProducts([])
    setSearchQuery("")
    setCurrentPage(1)
    onOpenChange(false)
  }

  const handleClose = () => {
    setSelectedProducts([])
    setSearchQuery("")
    setCurrentPage(1)
    onOpenChange(false)
  }

  return (
    <>
      {/* 메인 모달 - Step 1: 제품 선택 */}
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="max-w-6xl max-h-[95vh] overflow-hidden p-0 [&>button]:hidden">
          {/* 헤더 */}
          <div className="flex items-center justify-between border-b border-border bg-white px-6 py-4">
            <h2 className="text-xl font-semibold text-text-primary">제품 등록</h2>
            <button
              onClick={handleClose}
              className="rounded-full p-1 hover:bg-muted transition-colors"
              aria-label="닫기"
            >
              <X className="h-5 w-5 text-text-primary" />
            </button>
          </div>

          {/* 바디 */}
          <div className="flex h-[calc(90vh-140px)] overflow-hidden">
            {/* 왼쪽: 제품 검색 & 목록 */}
            <div className="flex-1 border-r border-border overflow-y-auto">
              <div className="p-6 space-y-4">
                {/* 검색 입력 */}
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                  <Input
                    type="text"
                    placeholder="추가할 제품을 검색하세요"
                    value={searchQuery}
                    onChange={(e) => {
                      setSearchQuery(e.target.value)
                      setCurrentPage(1)
                    }}
                    className="pl-10"
                  />
                </div>

                {/* 제품 목록 */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {paginatedProducts.map((product) => (
                    <ProductSearchCard
                      key={product.id}
                      product={product}
                      isSelected={selectedProducts.some((p) => p.id === product.id)}
                      onToggleSelect={handleToggleSelect}
                    />
                  ))}
                </div>

                {/* 페이지네이션 */}
                {totalPages > 1 && (
                  <Pagination className="mt-6">
                    <PaginationContent>
                      <PaginationItem>
                        <PaginationPrevious
                          onClick={() => setCurrentPage((prev) => Math.max(1, prev - 1))}
                          className={currentPage === 1 ? "pointer-events-none opacity-50" : "cursor-pointer"}
                        />
                      </PaginationItem>
                      {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                        <PaginationItem key={page}>
                          <PaginationLink
                            onClick={() => setCurrentPage(page)}
                            isActive={currentPage === page}
                            className="cursor-pointer"
                          >
                            {page}
                          </PaginationLink>
                        </PaginationItem>
                      ))}
                      <PaginationItem>
                        <PaginationNext
                          onClick={() => setCurrentPage((prev) => Math.min(totalPages, prev + 1))}
                          className={currentPage === totalPages ? "pointer-events-none opacity-50" : "cursor-pointer"}
                        />
                      </PaginationItem>
                    </PaginationContent>
                  </Pagination>
                )}

                {/* 검색 결과 없음 */}
                {paginatedProducts.length === 0 && (
                  <div className="py-12 text-center">
                    <p className="text-sm text-text-secondary">검색 결과가 없습니다</p>
                  </div>
                )}
              </div>
            </div>

            {/* 오른쪽: 선택된 제품 */}
            <div className="w-80 flex flex-col bg-muted/20">
              <div className="flex-shrink-0 border-b border-border bg-white px-4 py-3">
                <h3 className="text-sm font-semibold text-text-primary">
                  선택된 제품 ({selectedProducts.length})
                </h3>
              </div>
              <div className="flex-1 overflow-y-auto p-4 space-y-2">
                {selectedProducts.length === 0 ? (
                  <div className="py-12 text-center">
                    <p className="text-sm text-text-secondary">
                      선택된 제품이 없습니다
                    </p>
                  </div>
                ) : (
                  selectedProducts.map((product) => (
                    <SelectedProductItem
                      key={product.id}
                      product={product}
                      onRemove={handleRemoveSelected}
                    />
                  ))
                )}
              </div>
            </div>
          </div>

          {/* 푸터 */}
          <div className="flex-shrink-0 border-t border-border bg-white px-6 py-4">
            <Button
              onClick={handleConfirmSelection}
              disabled={selectedProducts.length === 0}
              className="w-full bg-primary-500 text-dark-bg hover:bg-primary-400 disabled:opacity-50"
            >
              선택 완료 ({selectedProducts.length})
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* Step 2: 추가 확인 모달 */}
      <ConfirmAddModal
        open={showConfirmModal}
        onOpenChange={setShowConfirmModal}
        selectedProducts={selectedProducts}
        onConfirm={handleFinalConfirm}
      />
    </>
  )
}

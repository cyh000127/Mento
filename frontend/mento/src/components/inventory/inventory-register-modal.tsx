import { useState, useEffect, useCallback } from "react";
import { Search, X, ChevronLeft, ChevronRight } from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { Pagination, PaginationContent, PaginationItem, PaginationLink } from "@/components/ui/pagination";
import { searchProducts } from "@/api/inventoryApi";
import { getProducts } from "@/api/productsApi";
import type { ProductListItem } from "@/types/product";
import type { Product } from "@/types/inventory";

interface InventoryRegisterModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: (selectedProducts: Product[]) => void;
}

// API 상품을 재고 상품으로 변환하는 헬퍼 함수
function convertApiProductToInventoryProduct(apiProduct: ProductListItem): Product {
  return {
    id: apiProduct.productId.toString(),
    productId: apiProduct.productId,
    name: apiProduct.name,
    brand: apiProduct.brandName,
    category: "skin", // 기본값
    image: apiProduct.imageUrl || "https://via.placeholder.com/150",
    purchaseDate: "",
    expirationDate: "",
    repurchaseCount: 0,
    status: "purchasing",
    purchaseLink: "",
    isFavorite: false,
  };
}

interface ProductSearchCardProps {
  product: ProductListItem;
  isSelected: boolean;
  onToggleSelect: (productId: number) => void;
}

function ProductSearchCard({ product, isSelected, onToggleSelect }: ProductSearchCardProps) {
  const handleCardClick = () => {
    onToggleSelect(product.productId);
  };

  return (
    <div
      onClick={handleCardClick}
      className={`group relative rounded-lg border-2 bg-white p-2.5 transition-all hover:shadow-md w-full max-w-full cursor-pointer ${isSelected ? "border-primary-500 bg-primary-100/30" : "border-border hover:border-primary-400/50"
        }`}
    >
      <div className="flex gap-2.5 w-full max-w-full">
        {/* 체크박스 */}
        <div className="flex items-start pt-0.5">
          <Checkbox
            checked={isSelected}
            onCheckedChange={() => onToggleSelect(product.productId)}
            className="data-[state=checked]:bg-primary-500 data-[state=checked]:border-primary-500"
          />
        </div>

        {/* 제품 이미지 */}
        <div className="h-14 w-14 flex-shrink-0 overflow-hidden rounded-md">
          <img src={product.imageUrl || "https://via.placeholder.com/150"} alt={product.name} className="h-full w-full object-cover" />
        </div>

        {/* 제품 정보 */}
        <div className="flex-1 min-w-0 overflow-hidden">
          <div className="mb-0.5">
            <span className="inline-block rounded-full bg-pastel-blue-200 px-2 py-0.5 text-xs font-medium text-text-primary">
              {product.categoryMedium}
            </span>
          </div>
          <h4 className="mb-0.5 text-sm font-semibold text-text-primary truncate" title={product.name}>{product.name}</h4>
          <p className="text-xs text-text-secondary truncate" title={product.brandName}>{product.brandName}</p>
        </div>
      </div>
    </div>
  );
}

interface SelectedProductItemProps {
  product: Product;
  onRemove: (productId: string) => void;
}

function SelectedProductItem({ product, onRemove }: SelectedProductItemProps) {
  return (
    <div className="flex items-center gap-3 rounded-lg border border-border bg-white p-2 transition-all hover:shadow-sm w-full max-w-full">
      {/* 제품 이미지 */}
      <div className="h-12 w-12 flex-shrink-0 overflow-hidden rounded">
        <img src={product.image} alt={product.name} className="h-full w-full object-cover" />
      </div>

      {/* 제품 정보 */}
      <div className="flex-1 min-w-0 overflow-hidden">
        <h4 className="text-sm font-medium text-text-primary truncate" title={product.name}>{product.name}</h4>
        <p className="text-xs text-text-secondary truncate" title={product.brand}>{product.brand}</p>
      </div>

      {/* 제거 버튼 */}
      <button onClick={() => onRemove(product.id)} className="flex-shrink-0 rounded-full p-1 hover:bg-muted transition-colors" aria-label="제거">
        <X className="h-4 w-4 text-text-secondary" />
      </button>
    </div>
  );
}

interface ConfirmAddModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  selectedProducts: Product[];
  onConfirm: () => void;
}

function ConfirmAddModal({ open, onOpenChange, selectedProducts, onConfirm }: ConfirmAddModalProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>제품 추가</DialogTitle>
        </DialogHeader>

        <div className="space-y-4 overflow-hidden">
          {/* 선택된 제품 요약 */}
          <div className="space-y-2 max-h-60 overflow-y-auto overflow-x-hidden">
            {selectedProducts.map((product) => (
              <div key={product.id} className="flex items-center gap-3 rounded-lg border border-border bg-muted/30 p-3 w-full max-w-full">
                <div className="h-12 w-12 flex-shrink-0 overflow-hidden rounded">
                  <img src={product.image} alt={product.name} className="h-full w-full object-cover" />
                </div>
                <div className="flex-1 min-w-0 overflow-hidden">
                  <h4 className="text-sm font-medium text-text-primary truncate" title={product.name}>{product.name}</h4>
                  <p className="text-xs text-text-secondary truncate" title={product.brand}>{product.brand}</p>
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
            <Button variant="outline" onClick={() => onOpenChange(false)} className="flex-1">
              취소
            </Button>
            <Button onClick={onConfirm} className="flex-1 bg-primary-500 text-dark-bg hover:bg-primary-400">
              추가
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}

export function InventoryRegisterModal({ open, onOpenChange, onConfirm }: InventoryRegisterModalProps) {
  // UI 상태
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedProducts, setSelectedProducts] = useState<Product[]>([]);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);

  // API 상태
  const [apiProducts, setApiProducts] = useState<ProductListItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [totalPages, setTotalPages] = useState(0);

  // 페이지 크기
  const itemsPerPage = 6;

  // API에서 상품 목록 조회 (검색어 기준 / 기본 목록)
  const fetchProducts = useCallback(async () => {
    const trimmedQuery = searchQuery.trim();
    setIsLoading(true);
    try {
      if (trimmedQuery.length >= 2) {
        const response = await searchProducts({
          keyword: trimmedQuery,
          page: currentPage - 1, // API는 0부터 시작
          size: itemsPerPage,
          sort: "relevance",
        });

        setApiProducts(response.content);
        setTotalPages(response.totalPages);
      } else {
        const response = await getProducts({
          page: currentPage - 1, // API는 0부터 시작
          size: itemsPerPage,
        });

        if (response.success && response.data) {
          setApiProducts(response.data.content);
          setTotalPages(response.data.totalPages);
        } else {
          setApiProducts([]);
          setTotalPages(0);
        }
      }
    } catch (error) {
      console.error("상품 목록 조회 에러:", error);
      setApiProducts([]);
      setTotalPages(0);
    } finally {
      setIsLoading(false);
    }
  }, [searchQuery, currentPage]);

  // 모달이 열리거나 검색어/페이지가 변경될 때 데이터 조회
  useEffect(() => {
    if (open) {
      fetchProducts();
    }
  }, [open, fetchProducts]);

  const handleToggleSelect = (productId: number) => {
    const apiProduct = apiProducts.find((p) => p.productId === productId);
    if (!apiProduct) return;

    const productIdStr = productId.toString();
    const isAlreadySelected = selectedProducts.some((p) => p.id === productIdStr);

    if (isAlreadySelected) {
      setSelectedProducts((prev) => prev.filter((p) => p.id !== productIdStr));
    } else {
      // 선택 시에만 Product 타입으로 변환
      const inventoryProduct = convertApiProductToInventoryProduct(apiProduct);
      setSelectedProducts((prev) => [...prev, inventoryProduct]);
    }
  };

  const handleRemoveSelected = (productId: string) => {
    setSelectedProducts((prev) => prev.filter((p) => p.id !== productId));
  };

  const handleConfirmSelection = () => {
    if (selectedProducts.length === 0) return;
    setShowConfirmModal(true);
  };

  const handleFinalConfirm = () => {
    onConfirm(selectedProducts);
    setShowConfirmModal(false);
    setSelectedProducts([]);
    setSearchQuery("");
    setCurrentPage(1);
    onOpenChange(false);
  };

  const handleClose = () => {
    setSelectedProducts([]);
    setSearchQuery("");
    setCurrentPage(1);
    onOpenChange(false);
  };

  return (
    <>
      {/* 메인 모달 - Step 1: 제품 선택 */}
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="max-w-6xl max-h-[90vh] overflow-hidden p-0 [&>button]:hidden">
          <DialogHeader className="sr-only">
            <DialogTitle>제품 등록</DialogTitle>
          </DialogHeader>
          {/* 헤더 */}
          <div className="flex items-center justify-between border-b border-border bg-white px-6 py-3">
            <h2 className="text-xl font-semibold text-text-primary">제품 등록</h2>
            <button onClick={handleClose} className="rounded-full p-1 hover:bg-muted transition-colors" aria-label="닫기">
              <X className="h-5 w-5 text-text-primary" />
            </button>
          </div>

          {/* 바디 */}
          <div className="flex h-[calc(90vh-170px)] overflow-hidden">
            {/* 왼쪽: 제품 검색 & 목록 */}
            <div className="flex-1 border-r border-border flex flex-col overflow-hidden">
              <div className="p-4 flex flex-col h-full space-y-3">
                {/* 검색 입력 */}
                <div className="relative flex-shrink-0">
                  <Search className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-muted-foreground" />
                  <Input
                    type="text"
                    placeholder="추가할 제품을 검색하세요"
                    value={searchQuery}
                    onChange={(e) => {
                      setSearchQuery(e.target.value);
                      setCurrentPage(1);
                    }}
                    className="pl-11 h-11 text-base"
                  />
                </div>

                {/* 제품 목록 */}
                <div className="overflow-y-auto">
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
                    {apiProducts.map((product) => (
                      <ProductSearchCard
                        key={product.productId}
                        product={product}
                        isSelected={selectedProducts.some((p) => p.id === product.productId.toString())}
                        onToggleSelect={handleToggleSelect}
                      />
                    ))}
                  </div>

                  {/* 검색 결과 없음 */}
                  {apiProducts.length === 0 && !isLoading && (
                    <div className="py-12 text-center">
                      <p className="text-sm text-text-secondary">검색 결과가 없습니다</p>
                    </div>
                  )}
                </div>

                {/* 페이지네이션 */}
                {totalPages > 1 && (
                  <Pagination className="flex-shrink-0 mt-4">
                    <PaginationContent>
                      <PaginationItem>
                        <button
                          onClick={() => setCurrentPage((prev) => Math.max(1, prev - 1))}
                          disabled={currentPage === 1}
                          className={`inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50 hover:bg-accent hover:text-accent-foreground h-9 w-9 ${currentPage === 1 ? "opacity-50" : "cursor-pointer"
                            }`}
                          aria-label="이전 페이지"
                        >
                          <ChevronLeft className="h-4 w-4" />
                        </button>
                      </PaginationItem>
                      {(() => {
                        const maxVisible = 5;
                        const pages: number[] = [];

                        if (totalPages <= maxVisible) {
                          // 전체 페이지가 5개 이하면 모두 표시
                          for (let i = 1; i <= totalPages; i++) {
                            pages.push(i);
                          }
                        } else {
                          // 현재 페이지 주변만 표시
                          if (currentPage <= 3) {
                            // 앞쪽에 있을 때
                            for (let i = 1; i <= maxVisible; i++) {
                              pages.push(i);
                            }
                          } else if (currentPage >= totalPages - 2) {
                            // 뒤쪽에 있을 때
                            for (let i = totalPages - maxVisible + 1; i <= totalPages; i++) {
                              pages.push(i);
                            }
                          } else {
                            // 중간에 있을 때
                            for (let i = currentPage - 2; i <= currentPage + 2; i++) {
                              pages.push(i);
                            }
                          }
                        }

                        return pages.map((page) => (
                          <PaginationItem key={page}>
                            <PaginationLink onClick={() => setCurrentPage(page)} isActive={currentPage === page} className="cursor-pointer">
                              {page}
                            </PaginationLink>
                          </PaginationItem>
                        ));
                      })()}
                      <PaginationItem>
                        <button
                          onClick={() => setCurrentPage((prev) => Math.min(totalPages, prev + 1))}
                          disabled={currentPage === totalPages}
                          className={`inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50 hover:bg-accent hover:text-accent-foreground h-9 w-9 ${currentPage === totalPages ? "opacity-50" : "cursor-pointer"
                            }`}
                          aria-label="다음 페이지"
                        >
                          <ChevronRight className="h-4 w-4" />
                        </button>
                      </PaginationItem>
                    </PaginationContent>
                  </Pagination>
                )}
              </div>
            </div>

            {/* 오른쪽: 선택된 제품 */}
            <div className="w-80 flex flex-col bg-muted/20">
              <div className="flex-shrink-0 border-b border-border bg-white px-4 py-3">
                <h3 className="text-sm font-semibold text-text-primary">선택된 제품 ({selectedProducts.length})</h3>
              </div>
              <div className="flex-1 overflow-y-auto p-4 space-y-2">
                {selectedProducts.length === 0 ? (
                  <div className="py-12 text-center">
                    <p className="text-sm text-text-secondary">선택된 제품이 없습니다</p>
                  </div>
                ) : (
                  selectedProducts.map((product) => <SelectedProductItem key={product.id} product={product} onRemove={handleRemoveSelected} />)
                )}
              </div>
            </div>
          </div>

          {/* 푸터 */}
          <div className="flex-shrink-0 border-t border-border bg-white px-6 py-3">
            <Button onClick={handleConfirmSelection} disabled={selectedProducts.length === 0} className="w-full bg-primary-500 text-dark-bg hover:bg-primary-400 disabled:opacity-50 h-10">
              선택 완료 ({selectedProducts.length})
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* Step 2: 추가 확인 모달 */}
      <ConfirmAddModal open={showConfirmModal} onOpenChange={setShowConfirmModal} selectedProducts={selectedProducts} onConfirm={handleFinalConfirm} />
    </>
  );
}

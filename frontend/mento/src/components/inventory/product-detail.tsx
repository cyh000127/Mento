import { useEffect, useState } from "react"
import { Star, ExternalLink, Trash2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import type { Product, ProductStatus } from "@/types/inventory"
import type { ItemStatus } from "@/types/inventory"
import { STATUS_LABELS, getAllowedStatusTransitions, mapUiStatusToApiStatus } from "@/api/inventoryApi"
import NoInventory from "@/assets/images/No_inventory.png"

interface ProductDetailProps {
  product: Product
  onToggleFavorite: (productId: string) => void
  onDelete: (productId: string) => void
  onStatusChange: (productId: string, newStatus: ItemStatus) => void
  loading?: boolean
  isEmpty?: boolean
}

const categoryLabels = {
  skin: "스킨",
  beauty: "뷰티",
  hair: "헤어",
}

const statusLabels: Record<ProductStatus, string> = {
  "in-use": "보유 중",
  unavailable: "사용 완료",
  purchasing: "구매 중",
  recommended: "추천 제품",
  "over-dated": "기한 만료",
}

const statusColors: Record<ProductStatus, string> = {
  "in-use": "bg-primary-100 text-primary-500 border-primary-200",
  unavailable: "bg-gray-100 text-gray-600 border-gray-200",
  purchasing: "bg-pastel-purple-100 text-purple-600 border-pastel-purple-200",
  recommended: "bg-pastel-green-100 text-green-600 border-pastel-green-200",
  "over-dated": "bg-red-100 text-red-600 border-red-200",
}

const STATUS_PURCHASING: ProductStatus = "purchasing"
const STATUS_UNAVAILABLE: ProductStatus = "unavailable"
const STATUS_OVER_DATED: ProductStatus = "over-dated"
const ITEM_STATUS_OWNED: ItemStatus = "OWNED"
const ITEM_STATUS_UNAVAILABLE: ItemStatus = "UNAVAILABLE"
const ITEM_STATUS_PURCHASING: ItemStatus = "PURCHASING"

export function ProductDetail({
  product,
  onToggleFavorite,
  onDelete,
  onStatusChange,
  loading = false,
  isEmpty = false,
}: ProductDetailProps) {
  const canInteract = !isEmpty && Boolean(product.id)
  const isPlaceholder = !product.id && !isEmpty
  const displayBrand = isEmpty ? "제품을 등록해 주세요" : product.brand
  const displayName = isEmpty ? "제품을 등록해 주세요" : product.name
  const displayCategory = isEmpty ? "제품을 등록해 주세요" : isPlaceholder ? "-" : categoryLabels[product.category]
  const displayPurchaseDate = isEmpty ? "제품을 등록해 주세요" : formatDate(product.purchaseDate)
  const displayExpirationDate = isEmpty ? "제품을 등록해 주세요" : formatDate(product.expirationDate)
  const displayDaysUntilExpiry = isEmpty
    ? "제품을 등록해 주세요"
    : product.daysUntilExpiry !== undefined
      ? formatDaysUntilExpiry(product.daysUntilExpiry)
      : ""
  const displayRepurchaseCount = isEmpty ? "제품을 등록해 주세요" : `${product.repurchaseCount}회`
  const displayStatusLabel = isEmpty ? "제품을 등록해 주세요" : isPlaceholder ? "-" : statusLabels[product.status]
  const imageSrc =
    isEmpty || !product.image ? NoInventory : product.image
  const imageAlt = isEmpty ? "제품을 등록해 주세요" : product.name

  // 현재 상태를 API 상태로 변환
  const currentApiStatus = mapUiStatusToApiStatus(product.status)
  // 허용된 상태 전환 목록 가져오기
  const allowedTransitions = getAllowedStatusTransitions(currentApiStatus)
  const allowedStatusButtons = allowedTransitions.filter(
    (targetStatus) => targetStatus === ITEM_STATUS_UNAVAILABLE,
  )
  const canChangeToInUse =
    (product.status === STATUS_OVER_DATED ||
      product.status === STATUS_PURCHASING ||
      product.status === STATUS_UNAVAILABLE) &&
    allowedTransitions.includes(ITEM_STATUS_OWNED)

  const [showPurchaseActions, setShowPurchaseActions] = useState(false)

  useEffect(() => {
    setShowPurchaseActions(false)
  }, [product.id])

  return (
    <Card className="sticky top-20 overflow-hidden shadow-sm">
      <CardContent className="p-0">
        {/* 제품 이미지 */}
        <div className="relative aspect-square w-full overflow-hidden bg-muted">
          <img
            src={imageSrc}
            alt={imageAlt}
            className="h-full w-full object-cover"
          />
          {loading && (
            <div className="absolute inset-0 flex items-center justify-center bg-background/50">
              <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-500 border-t-transparent"></div>
            </div>
          )}
        </div>

        {/* 제품 정보 */}
        <div className="space-y-4 p-6">
          {/* 브랜드와 즐겨찾기 */}
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-medium text-muted-foreground">
              {displayBrand}
            </h3>
            <button
              onClick={() => {
                if (!canInteract) return
                onToggleFavorite(product.id)
              }}
              className="transition-colors hover:scale-110"
              aria-label="즐겨찾기"
            >
              <Star
                className={`h-5 w-5 ${product.isFavorite
                  ? "fill-yellow-400 text-yellow-400"
                  : "text-gray-300"
                  }`}
              />
            </button>
          </div>

          {/* 제품 이름 */}
          <h2 className="text-xl font-bold text-foreground">{displayName}</h2>

          {/* 상세 정보 */}
          <div className="space-y-3 border-t border-border pt-4">
            <DetailRow label="카테고리" value={displayCategory} />
            <DetailRow label="구매일" value={displayPurchaseDate} />
            <DetailRow label="사용기한" value={displayExpirationDate} />
            {(isEmpty || product.daysUntilExpiry !== undefined) && (
              <DetailRow
                label="만료까지"
                value={displayDaysUntilExpiry}
              />
            )}
            <DetailRow label="재구매 횟수" value={displayRepurchaseCount} />
          </div>

          {/* 상태 */}
          <div className="space-y-3 border-t border-border pt-4">
            <div className="flex items-center gap-2">
              <span className="text-sm font-medium text-muted-foreground">상태</span>
              <span
                className={`rounded-full border px-3 py-1 text-sm font-medium ${statusColors[product.status]
                  }`}
              >
                {displayStatusLabel}
              </span>
            </div>

            {/* 상태 변경 버튼들 */}
            {(canChangeToInUse || allowedStatusButtons.length > 0) && (
              <div className="space-y-2">
                {canChangeToInUse && (
                  <Button
                    onClick={() => {
                      if (!canInteract) return
                      onStatusChange(product.id, ITEM_STATUS_OWNED)
                    }}
                    variant="outline"
                    className="w-full text-sm"
                  >
                    보유 중으로 변경
                  </Button>
                )}
                {allowedStatusButtons.map((targetStatus) => (
                  <Button
                    key={targetStatus}
                    onClick={() => {
                      if (!canInteract) return
                      onStatusChange(product.id, targetStatus)
                    }}
                    variant="outline"
                    className="w-full text-sm"
                  >
                    {STATUS_LABELS[targetStatus]}로 변경
                  </Button>
                ))}
              </div>
            )}
          </div>

          {/* 액션 */}
          <div className="space-y-2 border-t border-border pt-4">
            {!showPurchaseActions ? (
              <Button
                onClick={() => {
                  if (!canInteract || !product.purchaseLink) return
                  window.open(product.purchaseLink, "_blank")
                  setShowPurchaseActions(true)
                }}
                className="w-full bg-primary-500 text-dark-bg hover:bg-primary-400"
              >
                <ExternalLink className="mr-2 h-4 w-4" />
                구매 링크
              </Button>
            ) : (
              <div className="space-y-2">
                <Button
                  onClick={() => {
                    if (!canInteract) return
                    onStatusChange(product.id, ITEM_STATUS_PURCHASING)
                    setShowPurchaseActions(false)
                  }}
                  className="w-full bg-primary-500 text-dark-bg hover:bg-primary-400"
                >
                  구매중으로 변경
                </Button>
                <Button
                  onClick={() => {
                    if (!canInteract) return
                    setShowPurchaseActions(false)
                  }}
                  variant="outline"
                  className="w-full"
                >
                  취소
                </Button>
              </div>
            )}
            <Button
              onClick={() => {
                if (!canInteract) return
                onDelete(product.id)
              }}
              variant="outline"
              className="w-full border-destructive text-destructive hover:bg-destructive/10"
            >
              <Trash2 className="mr-2 h-4 w-4" />
              삭제
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}

function DetailRow({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex justify-between text-sm">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium text-foreground">{value}</span>
    </div>
  )
}

function formatDate(dateString: string): string {
  if (!dateString) return "-"

  const date = new Date(dateString)

  if (isNaN(date.getTime())) return "-"

  return date.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  })
}

function formatDaysUntilExpiry(days: number): string {
  if (days < 0) {
    return `만료됨 (${Math.abs(days)}일 경과)`
  } else if (days === 0) {
    return "오늘 만료"
  } else {
    return `D-${days}`
  }
}

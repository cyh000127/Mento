import { Star, ExternalLink, Trash2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import type { Product, ProductStatus } from "@/types/inventory"
import type { ItemStatus } from "@/types/inventory"
import { STATUS_LABELS, getAllowedStatusTransitions, mapUiStatusToApiStatus } from "@/api/inventoryApi"

interface ProductDetailProps {
  product: Product
  onToggleFavorite: (productId: string) => void
  onDelete: (productId: string) => void
  onStatusChange: (productId: string, newStatus: ItemStatus) => void
  loading?: boolean
}

const categoryLabels = {
  skin: "스킨",
  beauty: "뷰티",
  hair: "헤어",
}

const statusLabels: Record<ProductStatus, string> = {
  "in-use": "보유 중",
  unavailable: "사용 불가",
  purchasing: "구매 중",
  recommended: "추천 제품",
  "over-dated": "사용 완료",
}

const statusColors: Record<ProductStatus, string> = {
  "in-use": "bg-primary-100 text-primary-500 border-primary-200",
  unavailable: "bg-gray-100 text-gray-600 border-gray-200",
  purchasing: "bg-pastel-purple-100 text-purple-600 border-pastel-purple-200",
  recommended: "bg-pastel-green-100 text-green-600 border-pastel-green-200",
  "over-dated": "bg-red-100 text-red-600 border-red-200",
}

export function ProductDetail({
  product,
  onToggleFavorite,
  onDelete,
  onStatusChange,
  loading = false,
}: ProductDetailProps) {
  // 현재 상태를 API 상태로 변환
  const currentApiStatus = mapUiStatusToApiStatus(product.status)
  // 허용된 상태 전환 목록 가져오기
  const allowedTransitions = getAllowedStatusTransitions(currentApiStatus)

  return (
    <Card className="sticky top-20 overflow-hidden shadow-sm">
      <CardContent className="p-0">
        {/* Product Image */}
        <div className="relative aspect-square w-full overflow-hidden bg-muted">
          <img
            src={product.image}
            alt={product.name}
            className="h-full w-full object-cover"
          />
          {loading && (
            <div className="absolute inset-0 flex items-center justify-center bg-background/50">
              <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-500 border-t-transparent"></div>
            </div>
          )}
        </div>

        {/* Product Info */}
        <div className="space-y-4 p-6">
          {/* Brand and Favorite */}
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-medium text-muted-foreground">
              {product.brand}
            </h3>
            <button
              onClick={() => onToggleFavorite(product.id)}
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

          {/* Product Name */}
          <h2 className="text-xl font-bold text-foreground">{product.name}</h2>

          {/* Details */}
          <div className="space-y-3 border-t border-border pt-4">
            <DetailRow label="카테고리" value={categoryLabels[product.category]} />
            <DetailRow label="구매일" value={formatDate(product.purchaseDate)} />
            <DetailRow label="사용기한" value={formatDate(product.expirationDate)} />
            {product.daysUntilExpiry !== undefined && (
              <DetailRow
                label="만료까지"
                value={formatDaysUntilExpiry(product.daysUntilExpiry)}
              />
            )}
            <DetailRow label="재구매 횟수" value={`${product.repurchaseCount}회`} />
          </div>

          {/* Status */}
          <div className="space-y-3 border-t border-border pt-4">
            <div className="flex items-center gap-2">
              <span className="text-sm font-medium text-muted-foreground">상태</span>
              <span
                className={`rounded-full border px-3 py-1 text-sm font-medium ${statusColors[product.status]
                  }`}
              >
                {statusLabels[product.status]}
              </span>
            </div>

            {/* 상태 변경 버튼들 */}
            {allowedTransitions.length > 0 && (
              <div className="space-y-2">
                {allowedTransitions.map((targetStatus) => (
                  <Button
                    key={targetStatus}
                    onClick={() => onStatusChange(product.id, targetStatus)}
                    variant="outline"
                    className="w-full text-sm"
                  >
                    {STATUS_LABELS[targetStatus]}로 변경
                  </Button>
                ))}
              </div>
            )}
          </div>

          {/* Actions */}
          <div className="space-y-2 border-t border-border pt-4">
            <Button
              onClick={() => window.open(product.purchaseLink, "_blank")}
              className="w-full bg-primary-500 text-dark-bg hover:bg-primary-400"
            >
              <ExternalLink className="mr-2 h-4 w-4" />
              구매 링크
            </Button>
            <Button
              onClick={() => onDelete(product.id)}
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

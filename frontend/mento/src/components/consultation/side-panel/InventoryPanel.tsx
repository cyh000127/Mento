import { useEffect, useMemo, useState } from "react"
import { useParams } from "react-router-dom"
import { useAuthStore } from "@/stores/useAuthStore"
import {
  getCustomerInventory,
  getInventoryItems,
  STATUS_LABELS,
} from "@/api/inventoryApi"
import { getReservationDetail } from "@/api/reservationApi"
import type { ApiItem } from "@/types/inventory"

const FALLBACK_IMAGE_URL = "https://via.placeholder.com/80"

export function InventoryPanel() {
  const { reservationId } = useParams<{ reservationId: string }>()
  const userRole = useAuthStore((state) => state.user?.role)
  const isConsultant = userRole === "MENTOR"

  const reservationIdNumber = useMemo(() => {
    if (!reservationId) return null
    const parsed = Number(reservationId)
    return Number.isNaN(parsed) ? null : parsed
  }, [reservationId])

  const [items, setItems] = useState<ApiItem[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let isMounted = true

    const fetchInventory = async () => {
      setLoading(true)
      setError(null)

      try {
        // ✅ 상담사 → 고객 인벤토리
        if (isConsultant && reservationIdNumber) {
          const reservationDetail = await getReservationDetail(reservationIdNumber)
          const customerUserId = reservationDetail.userInfo?.userId

          if (!customerUserId) {
            throw new Error("고객 정보를 찾을 수 없습니다.")
          }

          const inventory = await getCustomerInventory(customerUserId, {
            page: 0,
            size: 20,
          })

          if (isMounted) {
            setItems(inventory.content ?? [])
          }
        }
        // ✅ 일반 사용자 → 본인 인벤토리
        else {
          const inventory = await getInventoryItems({ page: 0, size: 20 })
          if (isMounted) {
            setItems(inventory.content ?? [])
          }
        }
      } catch (err: any) {
        console.error("인벤토리 조회 실패:", err)
        if (!isMounted) return

        const status = err?.response?.status
        setError(
          status === 403
            ? "권한이 없습니다."
            : status === 404
            ? "인벤토리를 찾을 수 없습니다."
            : "인벤토리 정보를 불러오지 못했습니다."
        )
        setItems([])
      } finally {
        if (isMounted) setLoading(false)
      }
    }

    fetchInventory()
    return () => {
      isMounted = false
    }
  }, [isConsultant, reservationIdNumber])

  return (
    <div className="flex h-full flex-col p-4 text-gray-200">
      <div className="mb-3">
        <h3 className="text-sm font-semibold text-gray-300">
          {isConsultant ? "고객 인벤토리" : "인벤토리"}
        </h3>
        {!isConsultant && (
          <p className="text-xs text-gray-400">상담 중 보유 제품 목록</p>
        )}
      </div>

      {loading && (
        <div className="flex flex-1 items-center justify-center text-sm text-gray-400">
          인벤토리 불러오는 중...
        </div>
      )}

      {!loading && error && (
        <div className="rounded-lg border border-red-500/30 bg-red-500/10 px-3 py-2 text-sm text-red-300">
          {error}
        </div>
      )}

      {!loading && !error && items.length === 0 && (
        <div className="flex flex-1 items-center justify-center text-sm text-gray-400">
          표시할 인벤토리가 없습니다.
        </div>
      )}

      {!loading && !error && items.length > 0 && (
        <div className="grid grid-cols-1 gap-3 overflow-y-auto pr-1">
          {items.map((item) => (
            <div
              key={item.id}
              className="flex gap-3 rounded-lg border border-gray-700 bg-gray-800 p-3"
            >
              <img
                src={item.productImageUrl || FALLBACK_IMAGE_URL}
                alt={item.productName}
                className="h-14 w-14 rounded object-cover"
              />

              <div className="min-w-0 flex-1">
                <div className="truncate text-sm font-semibold text-gray-100">
                  {item.productName}
                </div>
                <div className="truncate text-xs text-gray-400">
                  {item.brandName}
                </div>

                <div className="mt-1 flex items-center gap-2 text-xs text-gray-300">
                  <span className="rounded-full bg-gray-700 px-2 py-0.5">
                    {STATUS_LABELS[item.status] ?? item.status}
                  </span>
                  {item.expectedExpiryDate && (
                    <span className="text-gray-400">
                      사용기한 {formatDate(item.expectedExpiryDate)}
                    </span>
                  )}
                </div>
              </div>

              <div
                className={`text-xs ${
                  item.isFavorite ? "text-yellow-400" : "text-gray-500"
                }`}
              >
                {item.isFavorite ? "★" : "☆"}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

function formatDate(dateString?: string) {
  if (!dateString) return "-"
  const date = new Date(dateString)
  if (Number.isNaN(date.getTime())) return "-"
  return date.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  })
}

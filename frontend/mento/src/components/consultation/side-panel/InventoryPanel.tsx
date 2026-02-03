import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { useAuthStore } from "@/stores/useAuthStore";
import { getReservationDetail } from "@/api/reservationApi";
import { getCustomerInventory, STATUS_LABELS } from "@/api/inventoryApi";
import type { ApiItem } from "@/types/inventory";

const FALLBACK_IMAGE_URL = "https://via.placeholder.com/80";

export function InventoryPanel() {
  const { reservationId } = useParams<{ reservationId: string }>();
  const userRole = useAuthStore((state) => state.user?.role);
  const isConsultant = userRole === "MENTOR";

  const reservationIdNumber = useMemo(() => {
    if (!reservationId) return null;
    const parsed = Number(reservationId);
    return Number.isNaN(parsed) ? null : parsed;
  }, [reservationId]);

  const [items, setItems] = useState<ApiItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isConsultant || !reservationIdNumber) return;

    let isMounted = true;
    const loadCustomerInventory = async () => {
      setLoading(true);
      setError(null);

      try {
        const reservationDetail = await getReservationDetail(reservationIdNumber);
        const customerUserId = reservationDetail.userInfo?.userId;

        if (!customerUserId) {
          throw new Error("고객 정보를 찾을 수 없습니다.");
        }

        const inventory = await getCustomerInventory(customerUserId, {
          page: 0,
          size: 10,
        });

        if (!isMounted) return;
        setItems(inventory.content ?? []);
      } catch (fetchError: any) {
        if (!isMounted) return;
        const status = fetchError?.response?.status;
        const message =
          status === 403
            ? "권한이 없습니다."
            : status === 404
              ? "고객 정보를 찾을 수 없습니다."
              : "인벤토리 정보를 불러오지 못했습니다.";

        console.error("고객 인벤토리 조회 실패:", fetchError);
        setItems([]);
        setError(message);
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    loadCustomerInventory();

    return () => {
      isMounted = false;
    };
  }, [isConsultant, reservationIdNumber]);

  if (!isConsultant || !reservationIdNumber) return null;

  return (
    <div className="p-4">
      <div className="mb-3 text-sm font-semibold text-gray-200">고객 인벤토리</div>

      {loading && (
        <div className="flex items-center justify-center py-10 text-sm text-gray-400">
          인벤토리 불러오는 중...
        </div>
      )}

      {!loading && error && (
        <div className="rounded-lg border border-red-500/30 bg-red-500/10 px-3 py-2 text-sm text-red-300">
          {error}
        </div>
      )}

      {!loading && !error && items.length === 0 && (
        <div className="flex items-center justify-center py-10 text-sm text-gray-400">
          표시할 인벤토리가 없습니다.
        </div>
      )}

      {!loading && !error && items.length > 0 && (
        <div className="space-y-3">
          {items.map((item) => (
            <div
              key={item.id}
              className="flex gap-3 rounded-lg border border-gray-700 bg-gray-800 p-3"
            >
              <div className="h-16 w-16 flex-shrink-0 overflow-hidden rounded-md bg-gray-700">
                <img
                  src={item.productImageUrl || FALLBACK_IMAGE_URL}
                  alt={item.productName}
                  className="h-full w-full object-cover"
                />
              </div>
              <div className="min-w-0 flex-1">
                <div className="flex items-start justify-between gap-2">
                  <div className="min-w-0">
                    <div className="truncate text-sm font-semibold text-gray-100">
                      {item.productName}
                    </div>
                    <div className="truncate text-xs text-gray-400">
                      {item.brandName}
                    </div>
                  </div>
                  <div
                    className={`text-xs ${
                      item.isFavorite ? "text-yellow-400" : "text-gray-500"
                    }`}
                    aria-label={item.isFavorite ? "즐겨찾기" : "즐겨찾기 아님"}
                  >
                    {item.isFavorite ? "★" : "☆"}
                  </div>
                </div>
                <div className="mt-2 flex flex-wrap items-center gap-2 text-xs text-gray-300">
                  <span className="rounded-full bg-gray-700 px-2 py-0.5 text-gray-200">
                    {STATUS_LABELS[item.status] ?? item.status}
                  </span>
                  <span className="text-gray-400">
                    사용기한 {formatDate(item.expectedExpiryDate)}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function formatDate(dateString?: string): string {
  if (!dateString) return "-";
  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) return "-";
  return date.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
}

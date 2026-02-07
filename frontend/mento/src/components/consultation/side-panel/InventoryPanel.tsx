import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { useAuthStore } from "@/stores/useAuthStore";
import { addCustomerInventoryItem, addInventoryItem, getCustomerInventory, getInventoryItems, getAddInventoryErrorMessage, STATUS_LABELS } from "@/api/inventoryApi";
import { getReservationDetail } from "@/api/reservationApi";
import { InventoryRegisterModal } from "@/components/inventory/inventory-register-modal";
import type { ApiItem, Product } from "@/types/inventory";
import { AlertModal } from "@/components/common/alert-modal";
import { ConfirmModal } from "@/components/common/confirm-modal";
import type { AlertModalType } from "@/components/common/alert-modal";

const FALLBACK_IMAGE_URL = "https://via.placeholder.com/80";

const parseItemDate = (value?: string) => {
  if (!value) return NaN;
  return Date.parse(value);
};

const getItemTimestamp = (item: ApiItem) => {
  const createdAt = parseItemDate(item.createdAt);
  if (!Number.isNaN(createdAt)) return createdAt;
  const updatedAt = parseItemDate(item.updatedAt);
  if (!Number.isNaN(updatedAt)) return updatedAt;
  const purchaseDate = parseItemDate(item.purchaseDate);
  if (!Number.isNaN(purchaseDate)) return purchaseDate;
  return 0;
};

const sortInventoryByNewest = (items: ApiItem[]) => {
  const timestamps = items.map(getItemTimestamp);
  const hasValidTimestamp = timestamps.some((value) => Number.isFinite(value) && value > 0);
  if (!hasValidTimestamp) return [...items].reverse();
  return [...items].sort((a, b) => getItemTimestamp(b) - getItemTimestamp(a));
};

export function InventoryPanel() {
  const { roomId } = useParams<{ roomId: string }>();
  const userRole = useAuthStore((state) => state.user?.role);
  const isConsultant = userRole === "MENTOR";

  const reservationId = useMemo(() => {
    if (!roomId) return null;
    try {
      return atob(roomId);
    } catch (e) {
      console.error("Invalid room ID format", e);
      return null;
    }
  }, [roomId]);

  const reservationIdNumber = useMemo(() => {
    if (!reservationId) return null;
    const parsed = Number(reservationId);
    return Number.isNaN(parsed) ? null : parsed;
  }, [reservationId]);

  const [items, setItems] = useState<ApiItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [registerModalOpen, setRegisterModalOpen] = useState(false);
  const [customerUserId, setCustomerUserId] = useState<number | null>(null);
  const confirmResolverRef = useRef<((value: boolean) => void) | null>(null);
  const [alertState, setAlertState] = useState({
    open: false,
    title: "알림",
    message: "",
    type: "info" as AlertModalType,
    confirmText: "확인",
  });
  const [confirmState, setConfirmState] = useState({
    open: false,
    title: "확인",
    message: "",
    type: "warning" as AlertModalType,
    confirmText: "확인",
    cancelText: "취소",
  });

  const showAlert = (options: { title?: string; message: string; type?: AlertModalType; confirmText?: string }) => {
    setAlertState({
      open: true,
      title: options.title ?? "알림",
      message: options.message,
      type: options.type ?? "info",
      confirmText: options.confirmText ?? "확인",
    });
  };

  const requestConfirm = (options: { title?: string; message: string; type?: AlertModalType; confirmText?: string; cancelText?: string }) =>
    new Promise<boolean>((resolve) => {
      confirmResolverRef.current = resolve;
      setConfirmState({
        open: true,
        title: options.title ?? "확인",
        message: options.message,
        type: options.type ?? "warning",
        confirmText: options.confirmText ?? "확인",
        cancelText: options.cancelText ?? "취소",
      });
    });

  const handleConfirmResult = (result: boolean) => {
    confirmResolverRef.current?.(result);
    confirmResolverRef.current = null;
    setConfirmState((prev) => ({ ...prev, open: false }));
  };

  const fetchInventory = useCallback(
    async (canUpdate?: () => boolean) => {
      setLoading(true);
      setError(null);

      try {
        // ✅ 상담사 → 고객 인벤토리
        if (isConsultant && reservationIdNumber) {
          const reservationDetail = await getReservationDetail(reservationIdNumber);
          const customerUserId = reservationDetail.userInfo?.id;

          if (!customerUserId) {
            throw new Error("고객 정보를 찾을 수 없습니다.");
          }

          setCustomerUserId(customerUserId);
          const inventory = await getCustomerInventory(customerUserId, reservationIdNumber, {
            page: 0,
            size: 100,
          });

          const sortedItems = sortInventoryByNewest(inventory.content ?? []);
          if (!canUpdate || canUpdate()) {
            setItems(sortedItems);
          }
          return sortedItems;
        }
        // ✅ 일반 사용자 → 본인 인벤토리
        const inventory = await getInventoryItems({ page: 0, size: 100 });
        if (!canUpdate || canUpdate()) {
          setItems(inventory.content ?? []);
        }
        setCustomerUserId(null);
        return inventory.content ?? [];
      } catch (err: any) {
        console.error("인벤토리 조회 실패:", err);
        if (canUpdate && !canUpdate()) return [];

        const status = err?.response?.status;
        setError(status === 403 ? "권한이 없습니다." : status === 404 ? "인벤토리를 찾을 수 없습니다." : "인벤토리 정보를 불러오지 못했습니다.");
        setItems([]);
        return [];
      } finally {
        if (!canUpdate || canUpdate()) setLoading(false);
      }
    },
    [isConsultant, reservationIdNumber]
  );

  useEffect(() => {
    let isMounted = true;
    const canUpdate = () => isMounted;

    fetchInventory(canUpdate);
    return () => {
      isMounted = false;
    };
  }, [fetchInventory]);

  const handleAddProduct = () => {
    setRegisterModalOpen(true);
  };

  const handleProductsAdded = async (selectedProducts: Product[]) => {
    try {
      const latestItems = await fetchInventory();

      const duplicateProducts: Product[] = [];
      const productsToAdd: Product[] = [];

      selectedProducts.forEach((selectedProduct) => {
        const isDuplicate = latestItems.some((existingItem) => existingItem.productName === selectedProduct.name);

        if (isDuplicate) {
          duplicateProducts.push(selectedProduct);
        } else {
          productsToAdd.push(selectedProduct);
        }
      });

      if (duplicateProducts.length > 0) {
        const duplicateNames = duplicateProducts.map((p) => `"${p.name}"`).join(", ");

        if (productsToAdd.length > 0) {
          const confirmMessage = `다음 상품은 이미 인벤토리에 존재합니다:\n${duplicateNames}\n\n나머지 ${productsToAdd.length}개 상품을 추가하시겠습니까?`;

          const confirmed = await requestConfirm({
            title: "상품 추가 확인",
            message: confirmMessage,
            type: "warning",
            confirmText: "추가",
            cancelText: "취소",
          });
          if (!confirmed) {
            return;
          }
        } else {
          showAlert({
            title: "중복 상품",
            message: `선택한 모든 상품이 이미 인벤토리에 존재합니다:\n${duplicateNames}`,
            type: "info",
          });
          return;
        }
      }

      if (productsToAdd.length === 0) {
        return;
      }

      const resolvedCustomerUserId = isConsultant ? customerUserId : null;

      if (isConsultant && !resolvedCustomerUserId) {
        showAlert({
          title: "고객 정보 없음",
          message: "고객 정보를 찾을 수 없습니다.",
          type: "error",
        });
        return;
      }

      const results = await Promise.allSettled(
        productsToAdd.map((product) => {

          const addRequest =
            isConsultant && resolvedCustomerUserId
              ? addCustomerInventoryItem(resolvedCustomerUserId, {
                  productId: parseInt(product.id),
                  reservationId: reservationIdNumber!,
                })
              : addInventoryItem({
                  productId: parseInt(product.id),
                });

          return addRequest
            .then(() => ({ product, success: true }))
            .catch((error) => ({
              product,
              success: false,
              error,
              errorMessage: getAddInventoryErrorMessage(error),
            }));
        })
      );

      const successResults = results.filter((r) => r.status === "fulfilled" && r.value.success).map((r) => (r as PromiseFulfilledResult<any>).value.product);

      const failedResults = results.filter((r) => r.status === "fulfilled" && !r.value.success).map((r) => (r as PromiseFulfilledResult<any>).value);

      const errorMessages: string[] = [];

      if (failedResults.length > 0) {
        const failedDetails = failedResults.map(({ product, errorMessage }) => `"${product.name}": ${errorMessage}`).join("\n");
        errorMessages.push(`다음 상품 추가 중 오류가 발생했습니다:\n${failedDetails}`);
      }

      if (successResults.length > 0 && (duplicateProducts.length > 0 || errorMessages.length > 0)) {
        const messages = [`${successResults.length}개의 상품이 추가되었습니다.`];
        if (duplicateProducts.length > 0) {
          const duplicateNames = duplicateProducts.map((p) => `"${p.name}"`).join(", ");
          messages.push(`${duplicateProducts.length}개의 중복 상품은 제외되었습니다:\n${duplicateNames}`);
        }
        if (errorMessages.length > 0) {
          messages.push(...errorMessages);
        }
        showAlert({
          title: "상품 추가 결과",
          message: messages.join("\n\n"),
          type: errorMessages.length > 0 ? "warning" : "success",
        });
      } else if (successResults.length > 0) {
        showAlert({
          title: "상품 추가 완료",
          message: `${successResults.length}개의 상품이 인벤토리에 추가되었습니다.`,
          type: "success",
        });
      } else if (errorMessages.length > 0) {
        showAlert({
          title: "상품 추가 실패",
          message: errorMessages.join("\n\n"),
          type: "error",
        });
      }

      await fetchInventory();
    } catch (error) {
      console.error("상품 추가 중 예상치 못한 오류:", error);
      showAlert({
        title: "상품 추가 실패",
        message: "상품 추가 중 오류가 발생했습니다.",
        type: "error",
      });
    }
  };

  return (
    <div className="flex h-full flex-col p-4 text-gray-200">
      <div className="mb-3 flex items-start justify-between gap-3">
        <div>
          <h3 className="text-sm font-semibold text-gray-300">{isConsultant ? "고객 인벤토리" : "인벤토리"}</h3>
          {!isConsultant && <p className="text-xs text-gray-400">상담 중 보유 제품 목록</p>}
        </div>

        <button type="button" onClick={handleAddProduct} className="rounded-lg border border-gray-700 bg-gray-800 px-3 py-2 text-xs text-gray-200 transition-colors hover:bg-gray-700">
          제품 등록
        </button>
      </div>

      {loading && <div className="flex flex-1 items-center justify-center text-sm text-gray-400">인벤토리 불러오는 중...</div>}

      {!loading && error && <div className="rounded-lg border border-red-500/30 bg-red-500/10 px-3 py-2 text-sm text-red-300">{error}</div>}

      {!loading && !error && items.length === 0 && <div className="flex flex-1 items-center justify-center text-sm text-gray-400">표시할 인벤토리가 없습니다.</div>}

      {!loading && !error && items.length > 0 && (
        <div className="grid grid-cols-1 gap-3 overflow-y-auto pr-1">
          {items.map((item) => (
            <div key={item.itemId} className="flex gap-3 rounded-lg border border-gray-700 bg-gray-800 p-3">
              <img src={item.productImageUrl || FALLBACK_IMAGE_URL} alt={item.productName} className="h-14 w-14 rounded object-cover" />

              <div className="min-w-0 flex-1">
                <div className="truncate text-sm font-semibold text-gray-100">{item.productName}</div>
                <div className="truncate text-xs text-gray-400">{item.brandName}</div>

                <div className="mt-1 flex items-center gap-2 text-xs text-gray-300">
                  <span className="rounded-full bg-gray-700 px-2 py-0.5">{STATUS_LABELS[item.status] ?? item.status}</span>
                  {item.expectedExpiryDate && <span className="text-gray-400">사용기한 {formatDate(item.expectedExpiryDate)}</span>}
                </div>
              </div>

              <div className={`text-xs ${item.isFavorite ? "text-yellow-400" : "text-gray-500"}`}>{item.isFavorite ? "★" : "☆"}</div>
            </div>
          ))}
        </div>
      )}

      <InventoryRegisterModal open={registerModalOpen} onOpenChange={setRegisterModalOpen} onConfirm={handleProductsAdded} />

      <AlertModal
        open={alertState.open}
        onOpenChange={(open) => setAlertState((prev) => ({ ...prev, open }))}
        title={alertState.title}
        message={alertState.message}
        type={alertState.type}
        confirmText={alertState.confirmText}
      />

      <ConfirmModal
        open={confirmState.open}
        onOpenChange={(open) => {
          if (!open) handleConfirmResult(false);
        }}
        onConfirm={() => handleConfirmResult(true)}
        title={confirmState.title}
        message={confirmState.message}
        type={confirmState.type}
        confirmText={confirmState.confirmText}
        cancelText={confirmState.cancelText}
      />
    </div>
  );
}

function formatDate(dateString?: string) {
  if (!dateString) return "-";
  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) return "-";
  return date.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
}

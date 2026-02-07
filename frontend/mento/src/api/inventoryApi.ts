import { api, ocrApi } from "./axios";
import type {
  InventoryResponse,
  InventoryFilters,
  ApiItem,
  Product,
  ProductCategory,
  ProductStatus,
  ApiProductStatus,
  ItemStatus,
  ApiSortOption,
  SortOption,
  AddInventoryItemRequest,
  AddInventoryItemResponse,
  InventoryItemDetailResponse,
  InventoryHistoryFilters,
  InventoryHistoryResponse,
  ActionType,
  InventoryStatusUpdateResponse,
} from "@/types/inventory";
import type { ProductListItem } from "@/types/product";

export interface RecognizeProductResponse {
  status: "success" | "fail";
  message: string;
  ocr_text?: string;
  items?: ProductListItem[];
}

export interface ProductSearchParams {
  keyword: string;
  page?: number;
  size?: number;
}

export interface ProductSearchResponse {
  content: ProductListItem[];
  hasNext: boolean;
  totalPages: number;
  totalElements: number;
  page: number;
  size: number;
  isFirst: boolean;
  isLast: boolean;
}

/**
 * 인벤토리 목록 조회 API
 */
export async function getInventoryItems(filters: InventoryFilters = {}): Promise<InventoryResponse> {
  const params = new URLSearchParams();

  if (filters.page !== undefined) params.append("page", filters.page.toString());
  if (filters.size !== undefined) params.append("size", filters.size.toString());
  if (filters.category) params.append("category", filters.category);
  if (filters.status) params.append("status", filters.status);
  if (filters.isFavorite !== undefined) params.append("isFavorite", filters.isFavorite.toString());
  if (filters.sort) params.append("sort", filters.sort);

  const response = await api.get<InventoryResponse>(`/items?${params.toString()}`);
  return response.data;
}

/**
 * 상품 검색 API
 */
export async function searchProducts(params: ProductSearchParams): Promise<ProductSearchResponse> {
  const queryParams: Record<string, string | number> = {
    keyword: params.keyword,
  };

  if (params.page !== undefined) queryParams.page = params.page;
  if (params.size !== undefined) queryParams.size = params.size;

  const response = await api.get<ProductSearchResponse>("/products/search", {
    params: queryParams,
  });
  return response.data;
}

/**
 * 고객 인벤토리 목록 조회 API
 */
export async function getCustomerInventory(id: number, reservationId: number, filters: InventoryFilters = {}): Promise<InventoryResponse> {
  const params = new URLSearchParams();

  // 예약 ID 추가
  params.append("reservationId", reservationId.toString())

  if (filters.page !== undefined) params.append("page", filters.page.toString());
  if (filters.size !== undefined) params.append("size", filters.size.toString());
  if (filters.category) params.append("category", filters.category);
  if (filters.status) params.append("status", filters.status);
  if (filters.isFavorite !== undefined) params.append("isFavorite", filters.isFavorite.toString());
  if (filters.sort) params.append("sort", filters.sort);

  const response = await api.get<InventoryResponse>(`/users/${id}/items?${params.toString()}`);
  return response.data;
}

/**
 * 상태 전환 규칙 정의
 */
export const STATUS_TRANSITION_RULES: Record<ItemStatus, ItemStatus[]> = {
  OWNED: ["UNAVAILABLE", "PURCHASING", "OVER_DATED"],
  UNAVAILABLE: ["OWNED"],
  PURCHASING: ["OWNED"],
  RECOMMENDED: ["OWNED", "PURCHASING"],
  OVER_DATED: ["OWNED"],
};

/**
 * 한국어 상태 라벨 매핑
 */
export const STATUS_LABELS: Record<ItemStatus, string> = {
  OWNED: "보유 중",
  UNAVAILABLE: "사용 완료",
  PURCHASING: "구매 중",
  RECOMMENDED: "추천 제품",
  OVER_DATED: "기한 만료",
};

/**
 * 상태 전환 가능 여부 검증
 */
export function canTransitionStatus(currentStatus: ItemStatus, newStatus: ItemStatus): boolean {
  const allowedTransitions = STATUS_TRANSITION_RULES[currentStatus];
  return allowedTransitions?.includes(newStatus) ?? false;
}

/**
 * 현재 상태에서 전환 가능한 상태 목록 반환
 */
export function getAllowedStatusTransitions(currentStatus: ItemStatus): ItemStatus[] {
  return STATUS_TRANSITION_RULES[currentStatus] || [];
}

/**
 * API 상태를 UI 상태로 변환
 */
export function mapApiStatusToUiStatus(apiStatus: ApiProductStatus): ProductStatus {
  const statusMap: Record<ApiProductStatus, ProductStatus> = {
    OWNED: "in-use",
    UNAVAILABLE: "unavailable",
    PURCHASING: "purchasing",
    RECOMMENDED: "recommended",
    OVER_DATED: "over-dated",
  };
  return statusMap[apiStatus];
}

/**
 * UI 상태를 API 상태로 변환
 */
export function mapUiStatusToApiStatus(uiStatus: ProductStatus): ApiProductStatus {
  const statusMap: Record<ProductStatus, ApiProductStatus> = {
    "in-use": "OWNED",
    unavailable: "UNAVAILABLE",
    purchasing: "PURCHASING",
    recommended: "RECOMMENDED",
    "over-dated": "OVER_DATED",
  };
  return statusMap[uiStatus];
}

/**
 * UI 정렬 옵션을 API 정렬 옵션으로 변환
 */
export function mapUiSortToApiSort(uiSort: SortOption): ApiSortOption {
  const sortMap: Record<SortOption, ApiSortOption> = {
    recent: "LATEST",
    alphabetical: "OLDEST",
    expiring: "EXPIRING_SOON",
  };
  const apiSort = sortMap[uiSort];
  return apiSort;
}

/**
 * API 아이템을 Product 타입으로 변환
 */
export function mapApiItemToProduct(apiItem: ApiItem): Product {
  // categoryMedium을 UI category로 매핑
  const categoryMap: Record<string, ProductCategory> = {
    스킨케어: "skin",
    메이크업: "beauty",
    헤어케어: "hair",
  };

  const category = apiItem.categoryMedium ? categoryMap[apiItem.categoryMedium] || "skin" : "skin";

  return {
    id: apiItem.itemId.toString(),
    productId: apiItem.productId,
    name: apiItem.productName,
    brand: apiItem.brandName,
    category: category,
    image: apiItem.productImageUrl || "https://via.placeholder.com/150",
    purchaseDate: apiItem.purchaseDate || "",
    expirationDate: apiItem.expectedExpiryDate || "",
    repurchaseCount: 0, // API에 없는 필드, 기본값 사용
    status: mapApiStatusToUiStatus(apiItem.status),
    purchaseLink: "", // API에 없는 필드, 기본값 사용
    isFavorite: apiItem.isFavorite,
  };
}

/**
 * 인벤토리 아이템 추가 API
 */
export async function addInventoryItem(request: AddInventoryItemRequest): Promise<AddInventoryItemResponse> {
  const body: AddInventoryItemRequest = {
    productId: request.productId,
  };

  // purchaseDate가 제공된 경우에만 포함
  if (request.purchaseDate) {
    body.purchaseDate = request.purchaseDate;
  }

  try {
    const response = await api.post<AddInventoryItemResponse["data"]>("/items", body);

    return {
      success: true,
      data: response.data,
    };
  } catch (error: any) {
    console.error("인벤토리 추가 에러:", error);
    console.error("에러 상태:", error.response?.status);
    console.error("에러 응답:", error.response?.data);
    throw error;
  }
}

/**
 * 이미지 기반 상품 인식 API (OCR)
 */
export async function recognizeProductByImage(imageUrl: string): Promise<RecognizeProductResponse> {
  try {
    const response = await ocrApi.post<RecognizeProductResponse>("/products/recognize", { imageUrl });

    return response.data;
  } catch (error: any) {
    // 서버에서 내려준 에러 포맷이 있는 경우 그대로 반환
    if (error.response?.data) {
      return error.response.data as RecognizeProductResponse;
    }

    // 네트워크 에러 / 예상 못한 에러는 throw
    throw error;
  }
}

/**
 * 멘토가 고객 인벤토리 아이템 추가 API
 */
export async function addCustomerInventoryItem(userId: number, request: AddInventoryItemRequest): Promise<AddInventoryItemResponse> {
  const body: AddInventoryItemRequest = {
    productId: request.productId,
    reservationId: request.reservationId,
  };

  if (request.purchaseDate) {
    body.purchaseDate = request.purchaseDate;
  }

  try {
    const response = await api.post<AddInventoryItemResponse["data"]>(`/users/${userId}/items`, body);
    return {
      success: true,
      data: response.data,
    };
  } catch (error: any) {
    console.error("고객 인벤토리 추가 에러:", error);
    console.error("에러 상태:", error.response?.status);
    console.error("에러 응답:", error.response?.data);
    throw error;
  }
}

/**
 * 인벤토리 아이템 삭제 API (Soft Delete)
 */
export async function deleteInventoryItem(itemId: string): Promise<void> {
  try {
    await api.post(`/items/${itemId}`);
  } catch (error: any) {
    console.error("인벤토리 삭제 에러:", error);
    console.error("에러 상태:", error.response?.status);
    console.error("에러 응답:", error.response?.data);
    throw error;
  }
}

/**
 * 인벤토리 아이템 상세 정보 조회 API
 */
export async function getInventoryItemDetail(itemId: string): Promise<InventoryItemDetailResponse> {
  try {
    const response = await api.get<InventoryItemDetailResponse>(`/items/${itemId}`);
    return response.data;
  } catch (error: any) {
    console.error("인벤토리 상세 조회 에러:", error);
    console.error("에러 상태:", error.response?.status);
    console.error("에러 응답:", error.response?.data);
    throw error;
  }
}

/**
 * 인벤토리 아이템 즐겨찾기 토글 API
 */
export async function toggleInventoryItemFavorite(itemId: string): Promise<{ id: number; isFavorite: boolean }> {
  try {
    const response = await api.post<{ success: boolean; data: { id: number; isFavorite: boolean } }>(`/items/${itemId}/favorite`);
    return response.data.data;
  } catch (error: any) {
    console.error("즐겨찾기 토글 에러:", error);
    console.error("에러 상태:", error.response?.status);
    console.error("에러 응답:", error.response?.data);
    throw error;
  }
}

/**
 * 재고 아이템 상태 업데이트 API
 */
export async function updateInventoryItemStatus(itemId: number, itemStatus: ItemStatus): Promise<InventoryStatusUpdateResponse["data"]> {
  try {
    const response = await api.put<InventoryStatusUpdateResponse>(`/items/${itemId}`, null, {
      params: { itemStatus },
    });
    return response.data.data;
  } catch (error: any) {
    console.error("상태 업데이트 에러:", error);
    console.error("에러 상태:", error.response?.status);
    console.error("에러 응답:", error.response?.data);
    throw error;
  }
}

/**
 * 상태 업데이트 에러 메시지 변환
 */
export function getStatusUpdateErrorMessage(error: any): string {
  const status = error.response?.status;
  const errorCode = error.response?.data?.code || error.response?.data?.error;

  if (status === 400) {
    if (errorCode === "INVALID_STATUS") {
      return "유효하지 않은 상태입니다.";
    }
    if (errorCode === "INVALID_STATUS_TRANSITION") {
      return "현재 상태에서 선택한 상태로 변경할 수 없습니다.";
    }
    return "잘못된 요청입니다.";
  }

  if (status === 401) {
    return "로그인이 필요합니다.";
  }

  if (status === 403) {
    return "권한이 없습니다.";
  }

  if (status === 404) {
    return "아이템을 찾을 수 없습니다.";
  }

  return "상태 변경 중 오류가 발생했습니다.";
}

/**
 * 멘토가 고객 인벤토리 추가 에러 메시지 변환
 */
export function getAddInventoryErrorMessage(error: any): string {
  const status = error?.response?.status;
  const errorCode = error?.response?.data?.code || error?.response?.data?.error;

  if (status === 409 || errorCode === "ALREADY_IN_INVENTORY") {
    return "이미 인벤토리에 존재하는 제품입니다";
  }

  if (errorCode === "USER_NOT_FOUND") {
    return "사용자를 찾을 수 없습니다.";
  }

  if (errorCode === "PRODUCT_NOT_FOUND") {
    return "제품을 찾을 수 없습니다.";
  }

  if (status === 403) {
    return "권한이 없습니다.";
  }

  return "상품 추가 중 오류가 발생했습니다.";
}

/**
 * 액션 타입 한국어 라벨 매핑
 */
export const ACTION_TYPE_LABELS: Record<ActionType, string> = {
  CREATED: "추가",
  EXPIRED: "사용 기간 만료",
  DELETED: "삭제",
};

/**
 * 인벤토리 히스토리 조회 API
 */
export async function getInventoryHistories(filters: InventoryHistoryFilters = {}): Promise<InventoryHistoryResponse> {
  const params = new URLSearchParams();

  if (filters.page !== undefined) params.append("page", filters.page.toString());
  if (filters.size !== undefined) params.append("size", filters.size.toString());
  if (filters.productId !== undefined) params.append("productId", filters.productId.toString());
  if (filters.startDate) params.append("startDate", filters.startDate);
  if (filters.endDate) params.append("endDate", filters.endDate);

  try {
    const response = await api.get<InventoryHistoryResponse["data"]>(`/items/histories?${params.toString()}`);
    return {
      success: true,
      data: response.data,
    };
  } catch (error: any) {
    console.error("인벤토리 히스토리 조회 에러:", error);
    console.error("에러 상태:", error.response?.status);
    console.error("에러 응답:", error.response?.data);
    throw error;
  }
}

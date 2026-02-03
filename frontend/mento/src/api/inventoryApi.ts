import { api } from "./axios"
import type {
  InventoryResponse,
  InventoryFilters,
  ApiItem,
  Product,
  ProductCategory,
  ProductStatus,
  ApiProductStatus,
  ApiSortOption,
  SortOption,
  AddInventoryItemRequest,
  AddInventoryItemResponse,
  InventoryItemDetailResponse,
} from "@/types/inventory"

/**
 * 인벤토리 목록 조회 API
 */
export async function getInventoryItems(filters: InventoryFilters = {}): Promise<InventoryResponse> {
  const params = new URLSearchParams()

  if (filters.page !== undefined) params.append("page", filters.page.toString())
  if (filters.size !== undefined) params.append("size", filters.size.toString())
  if (filters.category) params.append("category", filters.category)
  if (filters.status) params.append("status", filters.status)
  if (filters.isFavorite !== undefined) params.append("isFavorite", filters.isFavorite.toString())
  if (filters.sort) params.append("sort", filters.sort)

  const response = await api.get<InventoryResponse>(`/items?${params.toString()}`)
  return response.data
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
  }
  return statusMap[apiStatus]
}

/**
 * UI 상태를 API 상태로 변환
 */
export function mapUiStatusToApiStatus(uiStatus: ProductStatus): ApiProductStatus {
  const statusMap: Record<ProductStatus, ApiProductStatus> = {
    "in-use": "OWNED",
    "unavailable": "UNAVAILABLE",
    "purchasing": "PURCHASING",
    "recommended": "RECOMMENDED",
    "over-dated": "OVER_DATED",
  }
  return statusMap[uiStatus]
}

/**
 * UI 정렬 옵션을 API 정렬 옵션으로 변환
 */
export function mapUiSortToApiSort(uiSort: SortOption): ApiSortOption {
  const sortMap: Record<SortOption, ApiSortOption> = {
    recent: "LATEST",
    alphabetical: "OLDEST",
    expiring: "EXPIRING_SOON",
  }
  return sortMap[uiSort]
}

/**
 * API 아이템을 Product 타입으로 변환
 */
export function mapApiItemToProduct(apiItem: ApiItem): Product {
  // categoryMedium을 UI category로 매핑
  const categoryMap: Record<string, ProductCategory> = {
    "스킨케어": "skin",
    "메이크업": "beauty",
    "헤어케어": "hair",
  }
  
  const category = apiItem.categoryMedium ? (categoryMap[apiItem.categoryMedium] || "skin") : "skin"
  
  return {
    id: apiItem.id.toString(),
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
  }
}

/**
 * 인벤토리 아이템 추가 API
 */
export async function addInventoryItem(request: AddInventoryItemRequest): Promise<AddInventoryItemResponse> {
  const body: AddInventoryItemRequest = {
    productId: request.productId,
  }

  // purchaseDate가 제공된 경우에만 포함
  if (request.purchaseDate) {
    body.purchaseDate = request.purchaseDate
  }

  try {
    const response = await api.post<AddInventoryItemResponse["data"]>("/items", body)
    console.log("인벤토리 추가 API 응답:", response.data, "상태:", response.status)
    
    return {
      success: true,
      data: response.data
    }
  } catch (error: any) {
    console.error("인벤토리 추가 에러:", error)
    console.error("에러 상태:", error.response?.status)
    console.error("에러 응답:", error.response?.data)
    throw error
  }
}

/**
 * 인벤토리 아이템 삭제 API (Soft Delete)
 */
export async function deleteInventoryItem(itemId: string): Promise<void> {
  try {
    await api.post(`/items/${itemId}`)
  } catch (error: any) {
    console.error("인벤토리 삭제 에러:", error)
    console.error("에러 상태:", error.response?.status)
    console.error("에러 응답:", error.response?.data)
    throw error
  }
}

/**
 * 인벤토리 아이템 상세 정보 조회 API
 */
export async function getInventoryItemDetail(itemId: string): Promise<InventoryItemDetailResponse> {
  try {
    const response = await api.get<InventoryItemDetailResponse>(`/items/${itemId}`)
    return response.data
  } catch (error: any) {
    console.error("인벤토리 상세 조회 에러:", error)
    console.error("에러 상태:", error.response?.status)
    console.error("에러 응답:", error.response?.data)
    throw error
  }
}

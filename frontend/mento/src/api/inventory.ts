import { api } from "./axios"
import type {
  InventoryResponse,
  InventoryFilters,
  ApiItem,
  Product,
  ProductStatus,
  ApiProductStatus,
  ApiSortOption,
  SortOption,
  AddInventoryItemRequest,
  AddInventoryItemResponse,
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
  return {
    id: apiItem.itemId.toString(),
    name: apiItem.name,
    brand: apiItem.brand,
    category: apiItem.category.toLowerCase() as Product["category"],
    image: apiItem.imageUrl,
    purchaseDate: apiItem.purchaseDate,
    expirationDate: apiItem.expirationDate,
    repurchaseCount: apiItem.repurchaseCount,
    status: mapApiStatusToUiStatus(apiItem.status),
    purchaseLink: apiItem.purchaseLink,
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

  const response = await api.post<AddInventoryItemResponse>("/items", body)
  return response.data
}

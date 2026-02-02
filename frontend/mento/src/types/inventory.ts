export type ProductCategory = "skin" | "beauty" | "hair"

export type ProductStatus = "in-use" | "unavailable" | "purchasing" | "recommended" | "over-dated"

// API에서 사용하는 상태 타입
export type ApiProductStatus = "OWNED" | "UNAVAILABLE" | "PURCHASING" | "RECOMMENDED" | "OVER_DATED"

// API 정렬 옵션
export type ApiSortOption = "LATEST" | "OLDEST" | "EXPIRING_SOON"

export type SortOption = "recent" | "alphabetical" | "expiring"

export interface Product {
  id: string
  name: string
  brand: string
  category: ProductCategory
  image: string
  purchaseDate: string
  expirationDate: string
  repurchaseCount: number
  status: ProductStatus
  purchaseLink: string
  isFavorite: boolean
}

// API 응답 타입
export interface ApiItem {
  itemId: number
  name: string
  brand: string
  category: string
  imageUrl: string
  purchaseDate: string
  expirationDate: string
  repurchaseCount: number
  status: ApiProductStatus
  purchaseLink: string
  isFavorite: boolean
}

export interface InventoryResponse {
  content: ApiItem[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  hasNext: boolean
}

export interface InventoryFilters {
  page?: number
  size?: number
  category?: string
  status?: ApiProductStatus
  isFavorite?: boolean
  sort?: ApiSortOption
}

// 인벤토리 아이템 추가 요청 타입
export interface AddInventoryItemRequest {
  productId: number
  purchaseDate?: string // YYYY-MM-DD
}

// 인벤토리 아이템 추가 응답 타입
export interface AddInventoryItemResponse {
  success: boolean
  data: {
    id: number
    productId: number
    status: ApiProductStatus
    isFavorite: boolean
    purchaseDate: string
    expectedExpiryDate: string
    createdAt: string
    updatedAt: string
  }
}

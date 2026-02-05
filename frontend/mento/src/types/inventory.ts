export type ProductCategory = "skin" | "beauty" | "hair"

export type ProductStatus = "in-use" | "unavailable" | "purchasing" | "recommended" | "over-dated"

// API에서 사용하는 상태 타입 (ItemStatus와 동일)
export type ItemStatus = "OWNED" | "UNAVAILABLE" | "PURCHASING" | "RECOMMENDED" | "OVER_DATED"
export type ApiProductStatus = ItemStatus

// API 정렬 옵션
export type ApiSortOption = "LATEST" | "OLDEST" | "EXPIRING_SOON"

export type SortOption = "recent" | "alphabetical" | "expiring"

export interface Product {
  id: string
  productId: number // 상품 마스터 ID
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
  daysUntilExpiry?: number
}

// API 응답 타입 (백엔드 응답 형식)
export interface ApiItem {
  id: number
  productName: string
  productImageUrl: string
  brandName: string
  status: ApiProductStatus
  isFavorite: boolean
  categoryMedium?: string
  categorySmall?: string
  purchaseDate?: string
  expectedExpiryDate?: string
  daysUntilExpiry?: number
  createdAt?: string
  updatedAt?: string
}

export interface InventoryResponse {
  content: ApiItem[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  hasNext: boolean
  isFirst: boolean
  isLast: boolean
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
  reservationId?: number // 상담(멘토) 흐름에서 필요
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

// 인벤토리 아이템 상세 조회 응답 타입
export interface InventoryItemDetailResponse {
  success: boolean
  data: {
    id: number
    userId: number
    categoryMedium?: string
    productInfoDto: {
      id: number
      name: string
      imageUrl: string
      productUrl: string
      categoryMedium?: string
      brandName?: string
    }
    status: "OWNED" | "IN_USE" | "OVER_DATED"
    isFavorite: boolean
    purchaseDate: string
    expectedExpiry: string
    purchaseCount: number
  }
}

// 인벤토리 히스토리 액션 타입
export type ActionType = "CREATED" | "EXPIRED" | "DELETED"

// 인벤토리 히스토리 아이템
export interface InventoryHistoryItem {
  historyId: number
  userId: number
  productId: number
  productName: string
  brandName: string
  imageUrl: string
  actionType: ActionType
  actionDescription: string
  createdAt: string
}

// 인벤토리 히스토리 응답 타입
export interface InventoryHistoryResponse {
  success: boolean
  data: {
    content: InventoryHistoryItem[]
    page: number
    size: number
    totalElements: number
    totalPages: number
    hasNext: boolean
    isFirst: boolean
    isLast: boolean
  }
}

// 인벤토리 히스토리 필터 타입
export interface InventoryHistoryFilters {
  page?: number
  size?: number
  productId?: number
  startDate?: string // YYYY-MM-DD
  endDate?: string // YYYY-MM-DD
}

// API 상품 정보 타입 (백엔드 응답 형식)
export interface ApiProduct {
  productId: number
  oliveyoungGoodsNo: string
  brandId: number
  brandName: string
  categoryMedium: string
  categorySmall: string
  name: string
  volume: string
  description?: string
  ingredients?: string
  price: number
  imageUrl: string
  productUrl: string
  skinTypes?: string  // JSON 배열 문자열
  relatedConditions?: string  // JSON 배열 문자열
  benefits?: string  // JSON 배열 문자열
  defaultUsageDays?: number
  createdAt: string
  updatedAt?: string
}

// 페이지네이션 응답 타입
export interface PaginatedResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

// 공통 API 응답 래퍼 타입
export interface ApiResponse<T> {
  success: boolean
  data: T
  error?: {
    code: string
    message: string
  }
  timestamp: string
}

// 상품 목록 아이템 타입
export interface ProductListItem {
  productId: number
  name: string
  brandName: string
  categoryMedium: string
  imageUrl: string
}

// 상품 목록 조회 파라미터
export interface ProductListParams {
  page?: number
  size?: number
}

// 정렬 옵션 타입
export type SortKey = "price" | "name" | "created_at"
export type SortOrder = "asc" | "desc"

// 카테고리 타입 (실제 카테고리 값은 백엔드 응답에 따라 수정 필요)
export type CategoryMedium = string
export type CategorySmall = string

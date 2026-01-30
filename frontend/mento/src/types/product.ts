// API 상품 정보 타입
export interface ApiProduct {
  id: number
  name: string
  brand: string
  categoryMedium: string
  categorySmall: string
  price: number
  volume: string
  imageUrl: string
  productUrl: string
  createdAt: string
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

// 상품 목록 조회 파라미터
export interface ProductListParams {
  brand?: string
  category_medium?: string
  category_small?: string
  sort_key?: "price" | "name" | "created_at"
  order?: "asc" | "desc"
  page?: number
  size?: number
}

// 정렬 옵션 타입
export type SortKey = "price" | "name" | "created_at"
export type SortOrder = "asc" | "desc"

// 카테고리 타입 (실제 카테고리 값은 백엔드 응답에 따라 수정 필요)
export type CategoryMedium = string
export type CategorySmall = string

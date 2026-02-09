import { api } from "./axios"
import type {
  ProductListItem,
  PaginatedResponse,
  ApiResponse,
  ProductListParams,
} from "@/types/product"

/**
 * 상품 목록 조회 API
 * @param params 조회 파라미터 (page, size)
 * @returns 페이지네이션된 상품 목록
 */
export async function getProducts(
  params?: ProductListParams
): Promise<ApiResponse<PaginatedResponse<ProductListItem>>> {
  try {
    // 값이 있는 파라미터만 쿼리 스트링에 포함
    const queryParams: Record<string, string | number> = {}

    if (params?.page !== undefined) queryParams.page = params.page
    if (params?.size !== undefined) queryParams.size = params.size

    const response = await api.get<ApiResponse<PaginatedResponse<ProductListItem>>>(
      "/products",
      { params: queryParams }
    )

    return response.data
  } catch (error: any) {
    // 에러 응답도 ApiResponse 형태로 반환
    if (error.response?.data) {
      return error.response.data
    }

    // 네트워크 에러 등의 경우
    throw error
  }
}

/**
 * 카테고리 목록 조회 (필요 시 구현)
 * 백엔드에 카테고리 목록 조회 API가 있다면 사용
 */
export async function getCategories(): Promise<{
  medium: string[]
  small: Record<string, string[]>
}> {
  // TODO: 실제 API가 있다면 구현
  // 임시로 빈 배열 반환
  return {
    medium: [],
    small: {},
  }
}

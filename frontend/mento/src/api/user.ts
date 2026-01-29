import { api } from "./axios"
import type { User, UserResponse } from "@/types/user"

export const userApi = {
  // 사용자 정보 조회
  getUserById: async (userId: number): Promise<User> => {
    try {
      const response = await api.get<UserResponse>(`/users/${userId}`)
      if (response.data.success && response.data.data) {
        return response.data.data
      }
      throw new Error(response.data.error?.message || "사용자 정보를 가져올 수 없습니다.")
    } catch (error) {
      console.error("사용자 정보 조회 실패:", error)
      throw error
    }
  },

  // 현재 로그인한 사용자 정보 조회 (토큰 기반)
  getCurrentUser: async (): Promise<User> => {
    try {
      // 백엔드에서 토큰으로 사용자 식별하는 엔드포인트가 있다면 사용
      // 없다면 토큰을 디코딩하여 userId를 추출해야 함
      const response = await api.get<UserResponse>("/users/me")
      if (response.data.success && response.data.data) {
        return response.data.data
      }
      throw new Error(response.data.error?.message || "사용자 정보를 가져올 수 없습니다.")
    } catch (error) {
      console.error("현재 사용자 정보 조회 실패:", error)
      throw error
    }
  },
}

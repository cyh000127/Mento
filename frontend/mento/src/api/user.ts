import { api } from "./axios"
import type { User, UserResponse } from "@/types/user"
import { getUserIdFromToken } from "@/utils/jwt"
import { useAuthStore } from "@/stores/useAuthStore"

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
      // accessToken에서 userId 추출
      const token = useAuthStore.getState().accessToken
      if (!token) {
        throw new Error("로그인이 필요합니다.")
      }

      const userId = getUserIdFromToken(token)
      if (!userId) {
        throw new Error("토큰에서 사용자 ID를 추출할 수 없습니다.")
      }

      // userId로 사용자 정보 조회
      return await userApi.getUserById(userId)
    } catch (error) {
      console.error("현재 사용자 정보 조회 실패:", error)
      throw error
    }
  },
}

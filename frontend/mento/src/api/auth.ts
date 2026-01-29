import { api } from "./axios"
import { useAuthStore } from "@/stores/useAuthStore"

export const authApi = {
  /**
   * 로그아웃
   * - 백엔드에서 refreshToken 쿠키 제거
   * - accessToken을 블랙리스트에 추가
   */
  logout: async () => {
    try {
      await api.post("/auth/logout")
      // 로컬 상태 초기화
      useAuthStore.getState().logout()
    } catch (error) {
      console.error("로그아웃 실패:", error)
      // 실패해도 로컬 상태는 초기화
      useAuthStore.getState().logout()
      throw error
    }
  },

  /**
   * 토큰 재발급
   * - refreshToken 쿠키를 사용하여 새로운 accessToken 발급
   * - 새로운 refreshToken은 쿠키로 자동 설정됨
   */
  reissue: async () => {
    try {
      const response = await api.post("/auth/reissue")
      
      // 새로운 accessToken을 헤더에서 추출
      const authHeader = response.headers["authorization"]
      if (authHeader?.startsWith("Bearer ")) {
        const newAccessToken = authHeader.replace("Bearer ", "")
        useAuthStore.getState().setAccessToken(newAccessToken)
      }
      
      return response
    } catch (error) {
      console.error("토큰 재발급 실패:", error)
      throw error
    }
  },
}

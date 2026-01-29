import { api } from "./axios"

export const authApi = {
  // 로그아웃 (refreshToken 쿠키 제거)
  logout: async () => {
    try {
      await api.post("/auth/logout")
    } catch (error) {
      console.error("로그아웃 실패:", error)
      throw error
    }
  },

  // 토큰 재발급 (refreshToken 쿠키 기반)
  reissue: async () => {
    try {
      const response = await api.post("/auth/reissue")
      return response
    } catch (error) {
      console.error("토큰 재발급 실패:", error)
      throw error
    }
  },
}

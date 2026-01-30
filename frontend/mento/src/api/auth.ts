import { api } from "./axios"
import { useAuthStore } from "@/stores/useAuthStore"

// reissue 중복 호출 방지
let reissuePromise: Promise<unknown> | null = null

export const authApi = {
  /**
   * 로그아웃
   * - 백엔드에서 refreshToken 쿠키 제거
   * - accessToken을 블랙리스트에 추가
   */
  logout: async () => {
    try {
      await api.post("/auth/logout")
      localStorage.removeItem("hasRefreshToken")
      // 로컬 상태 초기화
      useAuthStore.getState().logout()
    } catch (error) {
      console.error("로그아웃 실패:", error)
      // 실패해도 로컬 상태는 초기화
      localStorage.removeItem("hasRefreshToken")
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
    if (reissuePromise) {
      return reissuePromise
    }

    reissuePromise = (async () => {
      try {
        const response = await api.post("/auth/reissue")

        // 새로운 accessToken을 헤더에서 추출 (우선)
        const authHeader = response.headers["authorization"]
        let newAccessToken: string | null = null
        if (authHeader?.startsWith("Bearer ")) {
          newAccessToken = authHeader.replace("Bearer ", "")
        }

        // 헤더에 없으면 응답 바디에서 추출 (백엔드 구현 차이 대응)
        if (!newAccessToken) {
          const dataToken =
            response.data?.data?.accessToken ??
            response.data?.accessToken ??
            response.data?.access_token
          if (typeof dataToken === "string" && dataToken) {
            newAccessToken = dataToken
          }
        }

        if (newAccessToken) {
          useAuthStore.getState().setAccessToken(newAccessToken)
          localStorage.setItem("hasRefreshToken", "true")
        } else {
          throw new Error("토큰 재발급 실패: accessToken을 받지 못했습니다.")
        }

        return response
      } catch (error) {
        console.error("토큰 재발급 실패:", error)
        throw error
      } finally {
        reissuePromise = null
      }
    })()

    return reissuePromise
  },
}

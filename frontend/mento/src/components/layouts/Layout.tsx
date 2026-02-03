import { useEffect, useRef } from "react"
import { Outlet } from "react-router-dom"
import { Header } from "@/components/header"
import { Footer } from "@/components/footer"
import { useAuthStore } from "@/stores/useAuthStore"
import { authApi } from "@/api/authApi"
import { userApi } from "@/api/userApi"

export default function Layout() {
  const { accessToken, user, setUser, logoutTriggered } = useAuthStore()
  const isFetchingRef = useRef(false)
  const reissueAttemptedRef = useRef(false)

  useEffect(() => {
    // 앱 초기화 시 토큰 복원 로직
    const initializeAuth = async () => {
      // 이미 요청 중이면 스킵 (중복 호출 방지)
      if (isFetchingRef.current) {
        return
      }

      isFetchingRef.current = true

      try {
        // Case 1: accessToken이 있는 경우 - 토큰 유효성 검증 (getCurrentUser 호출)
        if (accessToken) {
          try {
            // 사용자 정보 갱신 및 토큰 유효성 확인
            // 만약 토큰이 만료되었다면 axios interceptor가 갱신을 시도함
            const userData = await userApi.getCurrentUser()
            setUser(userData)
          } catch (error) {
            console.error("초기 토큰 검증 실패:", error)
            // 토큰이 유효하지 않고 갱신도 실패한 경우 로그아웃 처리 될 수 있음
            // (interceptors에서 처리되지만, 안전을 위해 확인)
          }
        }
        // Case 2: accessToken은 없지만 refreshToken이 있는 경우
        else {
          const hasRefreshTokenFlag = localStorage.getItem("hasRefreshToken") === "true"
          if (!logoutTriggered && hasRefreshTokenFlag) {
            if (!reissueAttemptedRef.current) {
              reissueAttemptedRef.current = true
              try {
                await authApi.reissue()
                const userData = await userApi.getCurrentUser()
                setUser(userData)
              } catch (error) {
                console.error("토큰 복원 실패:", error)
              }
            }
          }
        }
      } finally {
        isFetchingRef.current = false
        // 초기화 완료 표시
        useAuthStore.getState().setAuthInitialized(true)
      }
    }

    initializeAuth()
  }, [accessToken, setUser, logoutTriggered]) // accessToken이나 user가 변경될 때 실행

  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}

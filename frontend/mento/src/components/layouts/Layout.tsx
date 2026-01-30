import { useEffect, useRef } from "react"
import { Outlet } from "react-router-dom"
import { Header } from "@/components/header"
import { Footer } from "@/components/footer"
import { useAuthStore } from "@/stores/useAuthStore"
import { authApi } from "@/api/auth"
import { userApi } from "@/api/user"

export default function Layout() {
  const { accessToken, user, setUser, logoutTriggered } = useAuthStore()
  const isFetchingRef = useRef(false)
  const reissueAttemptedRef = useRef(false)

  useEffect(() => {
    // 앱 초기화 시 토큰 복원 로직
    const initializeAuth = async () => {
      // 이미 accessToken과 user 정보가 모두 있으면 스킵
      if (accessToken && user) {
        console.log("이미 로그인 정보가 있어 스킵합니다.")
        return
      }

      // 이미 요청 중이면 스킵 (중복 호출 방지)
      if (isFetchingRef.current) {
        console.log("이미 사용자 정보 조회 중입니다.")
        return
      }

      // accessToken은 있는데 user가 없는 경우 (KakaoCallback에서 온 경우)
      if (accessToken && !user) {
        console.log("사용자 정보 조회 시도 (로그인 후)")
        isFetchingRef.current = true
        try {
          const userData = await userApi.getCurrentUser()
          setUser(userData)
        } catch (userError) {
          console.error("사용자 정보 조회 실패:", userError)
        } finally {
          isFetchingRef.current = false
        }
        return
      }

      // accessToken도 없고 user도 없는 경우 - refreshToken으로 복원 시도
      const hasRefreshTokenFlag = localStorage.getItem("hasRefreshToken") === "true"

      if (!accessToken && !logoutTriggered && hasRefreshTokenFlag) {
        if (reissueAttemptedRef.current) {
          console.log("이미 토큰 재발급을 시도했습니다.")
          return
        }
        reissueAttemptedRef.current = true
        console.log("토큰 재발급 및 사용자 정보 복원 시도")
        isFetchingRef.current = true
        try {
          // refreshToken을 사용하여 새로운 accessToken 발급
          await authApi.reissue()
          // authApi.reissue()에서 이미 setAccessToken을 호출함

          // 사용자 정보 복원
          try {
            const userData = await userApi.getCurrentUser()
            setUser(userData)
          } catch (userError) {
            console.error("사용자 정보 복원 실패:", userError)
          }
        } catch (error) {
          console.error("토큰 복원 실패:", error)
        } finally {
          isFetchingRef.current = false
        }
      }
    }

    initializeAuth()
  }, [accessToken, user, setUser, logoutTriggered]) // accessToken이나 user가 변경될 때 실행

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

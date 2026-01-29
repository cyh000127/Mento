import { useEffect } from "react"
import { Outlet } from "react-router-dom"
import { Header } from "@/components/header"
import { Footer } from "@/components/footer"
import { useAuthStore } from "@/stores/useAuthStore"
import { authApi } from "@/api/auth"
import { userApi } from "@/api/user"
import { getCookie } from "@/utils/cookie"

export default function Layout() {
  const { accessToken, setUser } = useAuthStore()

  useEffect(() => {
    // 앱 초기화 시 토큰 복원 로직
    const initializeAuth = async () => {
      // 이미 accessToken이 메모리에 있으면 스킵
      if (accessToken) {
        return
      }

      // refreshToken 쿠키가 있는지 확인
      const hasRefreshToken = getCookie("refreshToken")
      
      if (hasRefreshToken) {
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
            // 사용자 정보 복원 실패해도 로그인은 유지
          }
        } catch (error) {
          console.error("토큰 복원 실패:", error)
          // 실패하면 로그아웃 상태 유지
        }
      }
    }

    initializeAuth()
  }, []) // 최초 마운트 시에만 실행

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

import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { useAuthStore } from "@/stores/useAuthStore"

export default function KakaoCallback() {
  const navigate = useNavigate()
  const { setAccessToken } = useAuthStore()
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const handleCallback = async () => {
      try {
        const params = new URLSearchParams(window.location.search)
        const accessToken = params.get("accessToken")
        const errorParam = params.get("error")

        // 에러가 있는 경우
        if (errorParam) {
          console.error("로그인 에러:", errorParam)
          setError("로그인에 실패했습니다. 다시 시도해주세요.")
          setTimeout(() => {
            navigate("/", { replace: true })
          }, 2000)
          return
        }

        // accessToken이 없는 경우
        if (!accessToken) {
          console.error("accessToken이 없습니다.")
          setError("로그인 정보를 받지 못했습니다.")
          setTimeout(() => {
            navigate("/", { replace: true })
          }, 2000)
          return
        }

        // accessToken을 메모리에 저장 (localStorage 사용 안 함)
        // refreshToken은 이미 쿠키로 설정되어 있음
        setAccessToken(accessToken)

        // 사용자 정보는 Layout에서 자동으로 조회됨
        // 여기서는 토큰만 저장하고 바로 리다이렉트

        // URL에서 토큰 제거 (보안을 위해)
        window.history.replaceState({}, "", "/")

        // 홈으로 리다이렉트 (Layout에서 사용자 정보를 조회함)
        navigate("/", { replace: true })
      } catch (err) {
        console.error("로그인 처리 중 오류:", err)
        setError("로그인 처리 중 오류가 발생했습니다.")
        setTimeout(() => {
          navigate("/", { replace: true })
        }, 2000)
      }
    }

    handleCallback()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []) // 최초 마운트 시에만 실행

  return (
    <div className="flex min-h-screen items-center justify-center bg-background">
      <div className="text-center">
        {error ? (
          <>
            <div className="mb-4 text-xl text-red-500">{error}</div>
            <div className="text-text-secondary">잠시 후 메인 페이지로 이동합니다...</div>
          </>
        ) : (
          <>
            <div className="mb-4 text-xl text-text-primary">로그인 처리 중...</div>
            <div className="text-text-secondary">잠시만 기다려주세요.</div>
          </>
        )}
      </div>
    </div>
  )
}

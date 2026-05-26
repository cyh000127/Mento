import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { authApi } from "@/api/authApi"

export default function KakaoCallback() {
  const navigate = useNavigate()
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const handleCallback = async () => {
      try {
        const params = new URLSearchParams(window.location.search)
        const errorParam = params.get("error")

        if (errorParam) {
          console.error("로그인 에러:", errorParam)
          setError("로그인에 실패했습니다. 다시 시도해주세요.")
          setTimeout(() => {
            navigate("/", { replace: true })
          }, 2000)
          return
        }

        try {
          await authApi.reissue()
          localStorage.setItem("hasRefreshToken", "true")
        } catch (reissueError) {
          console.error("토큰 재발급 실패:", reissueError)
          setError("로그인 정보를 받지 못했습니다.")
          setTimeout(() => {
            navigate("/", { replace: true })
          }, 2000)
          return
        }

        window.history.replaceState({}, "", "/")
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

import { useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { useAuthStore } from "@/stores/useAuthStore"

export default function KakaoCallback() {
  const navigate = useNavigate()
  const setAccessToken = useAuthStore((s) => s.setAccessToken)

  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const accessToken = params.get("accessToken")

    if (!accessToken) {
      navigate("/", { replace: true })
      return
    }

    setAccessToken(accessToken)

    // 토큰이 URL에 남지 않게 정리
    window.history.replaceState({}, "", "/")

    navigate("/", { replace: true })
  }, [navigate, setAccessToken])

  return <div>로그인 처리 중...</div>
}

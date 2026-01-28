import axios from "axios"
import { useAuthStore } from "@/stores/useAuthStore"

const API_BASE = import.meta.env.VITE_API_BASE_URL

export const api = axios.create({
  baseURL: `${API_BASE}/api/v1`,
  withCredentials: true, //  refreshToken 쿠키를 위해 필수
})

//  요청마다 accessToken 붙이기
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

//  401이면 reissue 후 재시도
api.interceptors.response.use(
  (res) => {
    // (선택) 백엔드가 Authorization 헤더로 새 토큰 내려줄 때 자동 반영
    const authHeader = res.headers["authorization"]
    if (authHeader?.startsWith("Bearer ")) {
      useAuthStore.getState().setAccessToken(authHeader.replace("Bearer ", ""))
    }
    return res
  },
  async (error) => {
    const original = error.config

    if (error.response?.status === 401 && !original?._retry) {
      original._retry = true
      try {
        await api.post("/auth/reissue") //  refreshToken 쿠키 기반
        return api(original)
      } catch {
        useAuthStore.getState().logout()
      }
    }

    return Promise.reject(error)
  }
)

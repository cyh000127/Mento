import { create } from "zustand"
import type { User } from "@/types/user"

interface AuthState {
  accessToken: string | null
  isLoggedIn: boolean
  user: User | null
  setAccessToken: (token: string) => void
  setUser: (user: User) => void
  logout: () => void
}

// 쿠키 기반 인증으로 변경 - localStorage 사용 안 함
export const useAuthStore = create<AuthState>()((set) => ({
  accessToken: null,
  isLoggedIn: false,
  user: null,

  setAccessToken: (token) =>
    set({
      accessToken: token,
      isLoggedIn: true,
    }),

  setUser: (user) =>
    set({
      user,
    }),

  logout: () =>
    set({
      accessToken: null,
      isLoggedIn: false,
      user: null,
    }),
}))

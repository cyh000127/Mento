import { create } from "zustand"
import { persist } from "zustand/middleware"
import type { User } from "@/types/user"

interface AuthState {
  accessToken: string | null
  isLoggedIn: boolean
  user: User | null
  setAccessToken: (token: string) => void
  setUser: (user: User) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
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
    }),
    {
      name: "auth-storage", // localStorage 키 이름
      partialize: (state) => ({
        accessToken: state.accessToken,
        isLoggedIn: state.isLoggedIn,
        user: state.user,
      }),
    }
  )
)

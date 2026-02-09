import { create } from "zustand"
import { persist } from "zustand/middleware"
import type { User } from "@/types/user"

interface AuthState {
  accessToken: string | null;
  isLoggedIn: boolean;
  user: User | null;
  logoutTriggered: boolean;
  isAuthInitialized: boolean;
  setAccessToken: (token: string) => void;
  setUser: (user: User) => void;
  setAuthInitialized: (initialized: boolean) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      isLoggedIn: false,
      user: null,
      logoutTriggered: false,
      isAuthInitialized: false,

      setAccessToken: (token) =>
        set({
          accessToken: token,
          isLoggedIn: true,
          logoutTriggered: false,
        }),

      setUser: (user) =>
        set({
          user,
        }),

      setAuthInitialized: (initialized) =>
        set({
          isAuthInitialized: initialized,
        }),

      logout: () =>
        set({
          accessToken: null,
          isLoggedIn: false,
          user: null,
          logoutTriggered: true,
          isAuthInitialized: true, // 로그아웃 시 초기화 완료 상태로 둠
        }),
    }),
    {
      name: "auth-storage",
      partialize: (state) => ({
        accessToken: state.accessToken,
        isLoggedIn: state.isLoggedIn,
        user: state.user,
        logoutTriggered: state.logoutTriggered,
      }),
    }
  )
)
import { create } from "zustand"

interface AuthState {
  accessToken: string | null
  isLoggedIn: boolean
  setAccessToken: (token: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  isLoggedIn: false,

  setAccessToken: (token) =>
    set({
      accessToken: token,
      isLoggedIn: true,
    }),

  logout: () =>
    set({
      accessToken: null,
      isLoggedIn: false,
    }),
}))

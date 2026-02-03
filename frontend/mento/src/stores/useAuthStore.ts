import { create } from "zustand";
import { persist } from "zustand/middleware"; // 추가
import type { User } from "@/types/user";

interface AuthState {
  accessToken: string | null;
  isLoggedIn: boolean;
  user: User | null;
  logoutTriggered: boolean;
  setAccessToken: (token: string) => void;
  setUser: (user: User) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      isLoggedIn: false,
      user: null,
      logoutTriggered: false,

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

      logout: () =>
        set({
          accessToken: null,
          isLoggedIn: false,
          user: null,
          logoutTriggered: true,
        }),
    }),
    {
      name: "auth-storage", // 저장될 키 이름
      // accessToken과 기본적인 로그인 상태만 유지하도록 설정 가능
    }
  )
);

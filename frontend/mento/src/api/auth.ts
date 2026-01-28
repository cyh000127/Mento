import { api } from "./axios"

export const authApi = {
  logout: () => api.post("/auth/logout"),
  reissue: () => api.post("/auth/reissue"),
}

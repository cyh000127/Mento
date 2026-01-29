export type UserRole = "USER" | "MENTOR"

export interface User {
  id: number
  email: string
  name: string
  birthDate: string
  role: UserRole
  createdAt: string
  updatedAt: string
}

export interface UserResponse {
  success: boolean
  data: User | null
  error: {
    status: string
    message: string
    method: string
    requestUri: string
    errors: string[]
  } | null
  timestamp: string
}

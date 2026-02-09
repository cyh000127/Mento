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

// 회원 정보 수정 요청 타입
export interface UpdateUserRequest {
  birthDate?: string // ISO 8601 형식 (YYYY-MM-DD), 선택적 필드
}

// 회원 정보 수정 응답 데이터 타입
export interface UpdateUserData {
  userId: number
  email: string
  birthdate: string
  updatedAt: string
}

// 회원 정보 수정 응답 타입
export interface UpdateUserResponse {
  success: boolean
  data: UpdateUserData | null
  error: {
    status: string
    message: string
    method: string
    requestUri: string
    errors: string[]
  } | null
  timestamp: string
}

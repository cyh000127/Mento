import { jwtDecode } from "jwt-decode"

interface JwtPayload {
  sub: string // 사용자 ID
  exp: number // 만료 시간
  iat: number // 발급 시간
  // 필요한 다른 필드 추가 가능
}

/**
 * JWT 토큰에서 사용자 ID를 추출합니다
 */
export function getUserIdFromToken(token: string): number | null {
  try {
    const decoded = jwtDecode<JwtPayload>(token)
    const userId = parseInt(decoded.sub, 10)
    return isNaN(userId) ? null : userId
  } catch (error) {
    console.error("JWT 디코딩 실패:", error)
    return null
  }
}

/**
 * JWT 토큰이 만료되었는지 확인합니다
 */
export function isTokenExpired(token: string): boolean {
  try {
    const decoded = jwtDecode<JwtPayload>(token)
    const currentTime = Date.now() / 1000
    return decoded.exp < currentTime
  } catch (error) {
    console.error("JWT 디코딩 실패:", error)
    return true
  }
}

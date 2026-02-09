/**
 * 쿠키 유틸리티
 * 백엔드의 CookieUtil을 참고하여 작성
 */

/**
 * 쿠키 값 조회
 */
export function getCookie(name: string): string | null {
  const cookies = document.cookie.split("; ")
  for (const cookie of cookies) {
    const [key, value] = cookie.split("=")
    if (key === name) {
      return decodeURIComponent(value)
    }
  }
  return null
}

/**
 * 쿠키 설정
 * 참고: HttpOnly 쿠키는 JavaScript에서 설정할 수 없으므로
 * refreshToken은 백엔드에서만 설정합니다.
 */
export function setCookie(name: string, value: string, maxAge?: number): void {
  let cookie = `${name}=${encodeURIComponent(value)}; path=/; SameSite=Lax`
  
  if (maxAge !== undefined) {
    cookie += `; max-age=${maxAge}`
  }
  
  // 프로덕션에서는 Secure 속성 추가
  if (window.location.protocol === "https:") {
    cookie += "; Secure"
  }
  
  document.cookie = cookie
}

/**
 * 쿠키 삭제
 */
export function deleteCookie(name: string): void {
  document.cookie = `${name}=; path=/; max-age=0`
}

/**
 * 모든 쿠키 삭제 (클라이언트에서 접근 가능한 쿠키만)
 */
export function deleteAllCookies(): void {
  const cookies = document.cookie.split("; ")
  for (const cookie of cookies) {
    const [name] = cookie.split("=")
    deleteCookie(name)
  }
}

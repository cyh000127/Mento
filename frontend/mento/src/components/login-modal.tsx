import { useEffect } from "react"
import { X } from "lucide-react"

interface LoginModalProps {
  isOpen: boolean
  onClose: () => void
}

export function LoginModal({ isOpen, onClose }: LoginModalProps) {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden"
    } else {
      document.body.style.overflow = "unset"
    }
    return () => {
      document.body.style.overflow = "unset"
    }
  }, [isOpen])

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-dark-bg/50 backdrop-blur-sm"
        onClick={onClose}
        onKeyDown={(e) => e.key === "Escape" && onClose()}
        role="button"
        tabIndex={0}
        aria-label="닫기"
      />

      {/* Modal */}
      <div className="relative z-10 mx-4 w-full max-w-md overflow-hidden rounded-2xl bg-background shadow-2xl">
        {/* Close Button */}
        <button
          type="button"
          onClick={onClose}
          className="absolute right-4 top-4 rounded-full p-1.5 text-text-secondary transition-colors hover:bg-muted hover:text-text-primary"
          aria-label="닫기"
        >
          <X className="h-5 w-5" />
        </button>

        {/* Content */}
        <div className="px-8 pb-8 pt-12">
          {/* Logo */}
          <div className="mb-8 text-center">
            <h2 className="text-2xl font-bold text-primary-500">MENTO</h2>
          </div>

          {/* Message */}
          <p className="mb-8 text-center text-base leading-relaxed text-text-primary">
            간편하게 로그인하고
            <br />
            다양한 서비스를 이용해보세요.
          </p>

          {/* Social Login Buttons */}
          <div className="flex flex-col gap-3">
            {/* Kakao Login */}
            <button
              type="button"
              onClick={() => {
                onClose()
                window.location.href =
                  `${import.meta.env.VITE_API_BASE_URL}/oauth2/authorization/kakao`
              }}
              className="flex w-full items-center justify-center gap-3 rounded-xl bg-[#FEE500] px-4 py-3.5 font-medium text-[#191919] transition-all hover:brightness-95"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 20 20"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                aria-hidden="true"
              >
                <path
                  fillRule="evenodd"
                  clipRule="evenodd"
                  d="M10 2.5C5.30558 2.5 1.5 5.52225 1.5 9.22727C1.5 11.5909 3.05025 13.6591 5.41667 14.8636L4.58333 18.0909C4.5 18.3182 4.75 18.5455 5 18.4091L8.83333 15.7727C9.21667 15.8182 9.61667 15.8636 10 15.8636C14.6944 15.8636 18.5 12.8409 18.5 9.22727C18.5 5.52225 14.6944 2.5 10 2.5Z"
                  fill="#191919"
                />
              </svg>
              카카오로 시작하기
            </button>

            {/* Divider */}
            <div className="my-2 flex items-center gap-3">
              <div className="h-px flex-1 bg-border" />
              <span className="text-xs text-text-secondary">또는</span>
              <div className="h-px flex-1 bg-border" />
            </div>

            {/* Google Login */}
            <button
              type="button"
              className="flex w-full items-center justify-center gap-3 rounded-xl border border-border bg-background px-4 py-3.5 font-medium text-text-primary transition-all hover:bg-muted"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 20 20"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                aria-hidden="true"
              >
                <path
                  d="M19.6 10.2273C19.6 9.51819 19.5364 8.83637 19.4182 8.18182H10V12.05H15.3818C15.15 13.3 14.4455 14.3591 13.3864 15.0682V17.5773H16.6182C18.5091 15.8364 19.6 13.2727 19.6 10.2273Z"
                  fill="#4285F4"
                />
                <path
                  d="M10 20C12.7 20 14.9636 19.1045 16.6182 17.5773L13.3864 15.0682C12.4909 15.6682 11.3455 16.0227 10 16.0227C7.39545 16.0227 5.19091 14.2636 4.40455 11.9H1.07727V14.4909C2.72273 17.7591 6.09091 20 10 20Z"
                  fill="#34A853"
                />
                <path
                  d="M4.40455 11.9C4.20455 11.3 4.09091 10.6636 4.09091 10C4.09091 9.33636 4.20455 8.7 4.40455 8.1V5.50909H1.07727C0.390909 6.85909 0 8.38636 0 10C0 11.6136 0.390909 13.1409 1.07727 14.4909L4.40455 11.9Z"
                  fill="#FBBC05"
                />
                <path
                  d="M10 3.97727C11.4682 3.97727 12.7864 4.48182 13.8227 5.47273L16.6909 2.60455C14.9591 0.990909 12.6955 0 10 0C6.09091 0 2.72273 2.24091 1.07727 5.50909L4.40455 8.1C5.19091 5.73636 7.39545 3.97727 10 3.97727Z"
                  fill="#EA4335"
                />
              </svg>
              Google로 시작하기
            </button>

            {/* Naver Login */}
            <button
              type="button"
              className="flex w-full items-center justify-center gap-3 rounded-xl bg-[#03C75A] px-4 py-3.5 font-medium text-white transition-all hover:brightness-95"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 20 20"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                aria-hidden="true"
              >
                <path
                  d="M13.5 10.5L6.25 0H0V20H6.5V9.5L13.75 20H20V0H13.5V10.5Z"
                  fill="white"
                />
              </svg>
              네이버로 시작하기
            </button>
          </div>

          {/* Terms */}
          <p className="mt-6 text-center text-xs leading-relaxed text-text-secondary">
            로그인 시{" "}
            <a href="/terms" className="underline hover:text-text-primary">
              이용약관
            </a>{" "}
            및{" "}
            <a href="/privacy" className="underline hover:text-text-primary">
              개인정보처리방침
            </a>
            에 동의합니다.
          </p>
        </div>
      </div>
    </div>
  )
}

import { useEffect } from "react";
import { X } from "lucide-react";

interface LoginModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export function LoginModal({ isOpen, onClose }: LoginModalProps) {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div className="absolute inset-0 bg-dark-bg/50 backdrop-blur-sm" onClick={onClose} onKeyDown={(e) => e.key === "Escape" && onClose()} role="button" tabIndex={0} aria-label="닫기" />

      {/* Modal */}
      <div className="relative z-10 mx-4 w-full max-w-md overflow-hidden rounded-2xl bg-background shadow-2xl">
        {/* Close Button */}
        <button type="button" onClick={onClose} className="absolute right-4 top-4 rounded-full p-1.5 text-text-secondary transition-colors hover:bg-muted hover:text-text-primary" aria-label="닫기">
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
  );
}

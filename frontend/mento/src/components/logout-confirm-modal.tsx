import { useEffect } from "react";

interface LogoutConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
}

export function LogoutConfirmModal({ isOpen, onClose, onConfirm }: LogoutConfirmModalProps) {
  // 모달이 열릴 때 스크롤 방지
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

  // ESC 키로 닫기
  useEffect(() => {
    if (!isOpen) return;

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    document.addEventListener("keydown", handleEscape);
    return () => document.removeEventListener("keydown", handleEscape);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* 딤드 배경 오버레이 */}
      <div
        className="absolute inset-0 bg-dark-bg/50 backdrop-blur-sm transition-opacity"
        onClick={onClose}
        aria-label="배경 클릭하여 닫기"
      />

      {/* 모달 컨테이너 */}
      <div className="relative z-10 mx-4 w-full max-w-md overflow-hidden rounded-2xl bg-white shadow-2xl animate-fade-in">
        {/* 컨텐츠 */}
        <div className="px-8 py-10">
          {/* 타이틀 */}
          <h2 className="mb-3 text-center text-2xl font-semibold text-dark-bg">
            로그아웃 하시겠습니까?
          </h2>

          {/* 설명 텍스트 */}
          <p className="mb-8 text-center text-sm text-muted-foreground">
            로그아웃하면 다시 로그인해야 합니다.
          </p>

          {/* 버튼 영역 */}
          <div className="flex gap-3">
            {/* 로그아웃 버튼 */}
            <button
              type="button"
              onClick={() => {
                onConfirm();
                onClose();
              }}
              className="flex-1 rounded-xl bg-gradient-to-r from-primary-500 to-primary-400 px-6 py-3 text-sm font-medium text-black shadow-sm transition-all hover:shadow-md hover:brightness-105"
            >
              로그아웃
            </button>

            {/* 취소 버튼 */}
            <button
              type="button"
              onClick={onClose}
              className="flex-1 rounded-xl bg-muted px-6 py-3 text-sm font-medium text-muted-foreground transition-all hover:bg-muted/80"
            >
              취소
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

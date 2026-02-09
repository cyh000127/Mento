import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { MyPageSidebar } from "@/components/mypage/mypage-sidebar";
import { userApi } from "@/api/userApi";
import { useAuthStore } from "@/stores/useAuthStore";
import { AlertTriangle } from "lucide-react";
import { ConfirmModal } from "@/components/common/confirm-modal";

export default function AccountWithdrawalPage() {
  const navigate = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);

  const handleWithdrawClick = () => {
    setIsModalOpen(true);
  };

  const handleConfirmWithdraw = async () => {
    if (isProcessing) return;

    setIsProcessing(true);

    try {
      // 회원 탈퇴 API 호출
      await userApi.withdrawAccount();
    } catch (error) {
      console.error("회원 탈퇴 처리 중 오류:", error);
    } finally {
      // 회원 탈퇴 후 프론트엔드에서만 로그아웃 처리
      // (백엔드에서 이미 토큰이 무효화되어 로그아웃 API 호출 시 401 에러 발생)
      localStorage.removeItem("hasRefreshToken");
      useAuthStore.getState().logout();
      navigate("/");
    }
  };

  return (
    <div className="flex min-h-screen bg-background justify-center">
      <div className="flex w-full max-w-[1200px]">
        <MyPageSidebar />
        <div className="flex-1">
          <div className="mx-auto max-w-7xl px-6 py-8">
            {/* Page Header */}
            <div className="pl-1">
              <h1 className="text-2xl font-bold text-foreground pb-3">회원 탈퇴</h1>
            </div>

            {/* Warning Card */}
            <div className="mb-6 rounded-xl border border-border bg-card p-6 shadow-sm">
              <div className="flex items-start gap-3 mb-4">
                <AlertTriangle className="h-6 w-6 text-red-500 flex-shrink-0 mt-1" />
                <div>
                  <h2 className="text-lg font-semibold text-foreground mb-2">회원 탈퇴 안내</h2>
                  <div className="space-y-2 text-sm text-muted-foreground">
                    <p>회원 탈퇴를 진행하시기 전에 아래 내용을 반드시 확인해 주세요.</p>
                  </div>
                </div>
              </div>

              <div className="mt-4 space-y-3 text-sm text-muted-foreground border-t border-border pt-4">
                <div className="flex items-start gap-2">
                  <span className="flex-shrink-0 w-1.5 h-1.5 rounded-full bg-red-500 mt-2"></span>
                  <p>
                    <span className="font-medium text-foreground">계정이 영구적으로 삭제되며, </span>
                    탈퇴 후 같은 계정으로 재가입하실 수 없습니다.
                  </p>
                </div>
                <div className="flex items-start gap-2">
                  <span className="flex-shrink-0 w-1.5 h-1.5 rounded-full bg-red-500 mt-2"></span>
                  <p>
                    <span className="font-medium text-foreground">탈퇴 후에는 계정을 복구할 수 없습니다. </span>
                    신중하게 결정해 주시기 바랍니다.
                  </p>
                </div>
                <div className="flex items-start gap-2">
                  <span className="flex-shrink-0 w-1.5 h-1.5 rounded-full bg-blue-500 mt-2"></span>
                  <p>
                    <span className="font-medium text-foreground">상담 및 결제 내역은 보존됩니다. </span>
                    법령에 따라 관련 기록은 일정 기간 보관됩니다.
                  </p>
                </div>
                <div className="flex items-start gap-2">
                  <span className="flex-shrink-0 w-1.5 h-1.5 rounded-full bg-yellow-500 mt-2"></span>
                  <p>
                    <span className="font-medium text-foreground">탈퇴 즉시 로그아웃됩니다. </span>
                    회원 탈퇴를 진행하면 자동으로 로그아웃 처리됩니다.
                  </p>
                </div>
              </div>
            </div>

            {/* Withdraw Button */}
            <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
              <div className="flex flex-col items-center justify-center py-8">
                <p className="text-center text-muted-foreground mb-6">정말로 회원 탈퇴를 진행하시겠습니까?</p>
                <button onClick={handleWithdrawClick} className="px-8 py-3 bg-red-500 hover:bg-red-600 text-white font-medium rounded-lg transition-colors">
                  회원 탈퇴하기
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <ConfirmModal
        open={isModalOpen}
        onOpenChange={setIsModalOpen}
        onConfirm={handleConfirmWithdraw}
        title="회원 탈퇴 확인"
        message={
          <div className="space-y-2">
            <p className="font-medium text-foreground">이 작업은 되돌릴 수 없습니다.</p>
            <p>회원 탈퇴를 진행하시면 계정이 영구적으로 삭제되며, 복구가 불가능합니다. 정말로 탈퇴하시겠습니까?</p>
          </div>
        }
        type="error"
        confirmText={isProcessing ? "처리 중..." : "탈퇴하기"}
        cancelText="취소"
        confirmDisabled={isProcessing}
        disableClose={isProcessing}
      />
    </div>
  );
}

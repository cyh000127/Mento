import { useEffect, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { approvePaymentReservation } from "@/api/paymentApproveApi";
import { authApi } from "@/api/authApi";
import { useAuthStore } from "@/stores/useAuthStore";

export default function PaymentCallbackPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const hasRequested = useRef(false);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const pgToken = params.get("pg_token");
    const savedPaymentId = localStorage.getItem("paymentId");
    const paymentId = savedPaymentId ? Number(savedPaymentId) : null;
    const accessToken = useAuthStore.getState().accessToken;

    if (!pgToken || !paymentId || Number.isNaN(paymentId)) {
      console.error("[결제 승인] 필수 값 누락", { pgToken, paymentId });
      navigate("/payments/fail");
      return;
    }

    if (hasRequested.current) return;
    hasRequested.current = true;

    const ensureToken = async () => {
      if (accessToken) return;
      if (localStorage.getItem("hasRefreshToken") === "true") {
        await authApi.reissue();
      }
    };

    ensureToken()
      .then(() => {
        console.log("[결제 승인] payload:", { paymentId, pgToken });
        return approvePaymentReservation({ paymentId, pgToken });
      })
      .then((data) => {
        if (data.reservationStatus === "CONFIRMED") {
          localStorage.removeItem("paymentId");
          navigate("/consultation", {
            state: { step: 5 },
          });
        } else {
          console.error("[결제 승인] 예약 상태 비정상:", data.reservationStatus);
          navigate("/payments/fail");
        }
      })
      .catch((error) => {
        const message = error instanceof Error ? error.message : String(error);
        console.error(message);
        navigate("/payments/fail");
      });
  }, [location.search, navigate]);

  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center">
      <h1 className="text-xl font-semibold text-text-primary">결제 확인 중입니다...</h1>
      <p className="mt-2 text-sm text-text-secondary">잠시만 기다려 주세요.</p>
    </div>
  );
}

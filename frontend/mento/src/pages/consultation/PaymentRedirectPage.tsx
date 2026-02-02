import { useEffect } from "react";
import { useLocation } from "react-router-dom";

interface PaymentRedirectState {
  redirectUrl?: string;
}

export default function PaymentRedirectPage() {
  const location = useLocation();
  const state = location.state as PaymentRedirectState | null;
  const redirectUrl = state?.redirectUrl;

  useEffect(() => {
    if (!redirectUrl) {
      console.error("[결제 준비] redirectUrl 누락");
      return;
    }

    const timeoutId = window.setTimeout(() => {
      window.location.href = redirectUrl;
    }, 600);

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [redirectUrl]);

  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center">
      <h1 className="text-xl font-semibold text-text-primary">결제 페이지로 이동 중입니다...</h1>
      <p className="mt-2 text-sm text-text-secondary">잠시만 기다려 주세요.</p>
    </div>
  );
}

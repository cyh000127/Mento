import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuthStore } from "@/stores/useAuthStore";

export default function ProtectedRoute() {
  const { isLoggedIn, accessToken, isAuthInitialized } = useAuthStore();
  const location = useLocation();

  // 인증 초기화 전에는 보호 컴포넌트를 렌더링하지 않음
  if (!isAuthInitialized) {
    return null;
  }

  if (!isLoggedIn || !accessToken) {
    return <Navigate to="/" replace state={{ from: location }} />;
  }

  return <Outlet />;
}

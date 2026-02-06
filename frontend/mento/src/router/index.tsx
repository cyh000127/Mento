import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Layout from "@/components/layouts/Layout";
import HomePage from "@/pages/home/HomePage";
import AiCarePage from "@/pages/ai-care/AiCarePage";
import ConsultationPage from "@/pages/consultation/ConsultationPage";
import PaymentRedirectPage from "@/pages/consultation/PaymentRedirectPage";
import PaymentCallbackPage from "@/pages/consultation/PaymentCallbackPage";
import PaymentFailPage from "@/pages/consultation/PaymentFailPage";
import { ConsultationRoomPage } from "@/pages/consultation/ConsultationRoomPage";
import GuidePage from "@/pages/guide/GuidePage";
import GuideDetailPage from "@/pages/guide/GuideDetailPage";
import MentoringPage from "@/pages/mentoring/MentoringPage";
import InventoryPage from "@/pages/inventory/InventoryPage";
import ConsultationManagementPage from "@/pages/mypage/ConsultationManagementPage";
import AiSkincareHistoryPage from "@/pages/mypage/AiSkincareHistoryPage";
import InventoryHistoryPage from "@/pages/mypage/InventoryHistoryPage";
import AccountWithdrawalPage from "@/pages/mypage/AccountWithdrawalPage";
import KakaoCallback from "@/pages/auth/KakaoCallback";
import TestLoginPage from "@/pages/test/TestLoginPage";
import AuthInitializer from "@/router/AuthInitializer";
import ProtectedRoute from "@/router/ProtectedRoute";

const router = createBrowserRouter([
  {
    element: <AuthInitializer />,
    children: [
      {
        path: "/",
        element: <Layout />,
        children: [
          {
            index: true,
            element: <HomePage />,
          },
          {
            path: "/login/oauth2/callback",
            element: <KakaoCallback />,
          },
        ],
      },
      {
        element: <ProtectedRoute />,
        children: [
          {
            path: "/",
            element: <Layout />,
            children: [
              {
                path: "ai-care",
                element: <AiCarePage />,
              },
              {
                path: "consultation",
                element: <ConsultationPage />,
              },
              {
                path: "consultation/payment-redirect",
                element: <PaymentRedirectPage />,
              },
              {
                path: "payments/redirect",
                element: <PaymentCallbackPage />,
              },
              {
                path: "payments/fail",
                element: <PaymentFailPage />,
              },
              {
                path: "guide",
                element: <GuidePage />,
              },
              {
                path: "guide/:category/:productType",
                element: <GuideDetailPage />,
              },
              {
                path: "mentoring",
                element: <MentoringPage />,
              },
              {
                path: "inventory",
                element: <InventoryPage />,
              },
              {
                path: "/mypage/consultations",
                element: <ConsultationManagementPage />,
              },
              {
                path: "/mypage/ai-skincare",
                element: <AiSkincareHistoryPage />,
              },
              {
                path: "/mypage/inventory-history",
                element: <InventoryHistoryPage />,
              },
              {
                path: "/mypage/account-withdrawal",
                element: <AccountWithdrawalPage />,
              },
            ],
          },
          {
            path: "/consultation-room/:roomId",
            element: <ConsultationRoomPage />,
          },
        ],
      },
      {
        path: "/test/testLogin",
        element: <TestLoginPage />,
      },
    ],
  },
]);

export default function AppRouter() {
  return <RouterProvider router={router} />;
}

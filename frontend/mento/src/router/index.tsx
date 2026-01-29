import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Layout from "@/components/layouts/Layout";
import HomePage from "@/pages/home/HomePage";
import AiCarePage from "@/pages/ai-care/AiCarePage";
import ConsultationPage from "@/pages/consultation/ConsultationPage";
import { ConsultationRoomPage } from "@/pages/consultation/ConsultationRoomPage";
import { LivekitTestPage } from "@/pages/consultation/LiveKitTestPage";
import GuidePage from "@/pages/guide/GuidePage";
import MentoringPage from "@/pages/mentoring/MentoringPage";
import RecommendPage from "@/pages/recommend/RecommendPage";
import InventoryPage from "@/pages/inventory/InventoryPage";
import ConsultationManagementPage from "@/pages/mypage/ConsultationManagementPage";
import AiSkincareHistoryPage from "@/pages/mypage/AiSkincareHistoryPage";
import KakaoCallback from "@/pages/auth/KakaoCallback";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        index: true,
        element: <HomePage />,
      },
      {
        path: "ai-care",
        element: <AiCarePage />,
      },
      {
        path: "consultation",
        element: <ConsultationPage />,
      },
      {
        path: "guide",
        element: <GuidePage />,
      },
      {
        path: "mentoring",
        element: <MentoringPage />,
      },
      {
        path: "recommend",
        element: <RecommendPage />,
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
        path: "/login/oauth2/callback",
        element: <KakaoCallback />,
      },
    ],
  },
  {
    path: "/consultation-room/:reservationId",
    element: <ConsultationRoomPage />,
  },
  {
    path: "/livekit-test",
    element: <LivekitTestPage />,
  },
]);

export default function AppRouter() {
  return <RouterProvider router={router} />;
}

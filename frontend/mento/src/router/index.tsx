import { createBrowserRouter, RouterProvider } from "react-router-dom"
import Layout from "@/components/layouts/Layout"
import HomePage from "@/pages/home/HomePage"
import AiCarePage from "@/pages/ai-care/AiCarePage"
import ConsultationPage from "@/pages/consultation/ConsultationPage"
import { ConsultationRoomPage } from "@/pages/consultation/ConsultationRoomPage"
import GuidePage from "@/pages/guide/GuidePage"
import MentoringPage from "@/pages/mentoring/MentoringPage"
import RecommendPage from "@/pages/recommend/RecommendPage"
import InventoryPage from "@/pages/inventory/InventoryPage"
import ConsultationManagementPage from "@/pages/mypage/ConsultationManagementPage"


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
      }
    ],
  },
  {
    path: "/consultation-room",
    element: <ConsultationRoomPage />,
  },
])

export default function AppRouter() {
  return <RouterProvider router={router} />
}

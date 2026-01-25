import { createBrowserRouter } from 'react-router-dom'
import Layout from '@/components/layouts/Layout'
import Home from '@/pages/Home'
import MyPage from '@/pages/MyPage'
import Recommend from '@/pages/Recommend'
import Mentoring from '@/pages/Mentoring'
import Guide from '@/pages/Guide'
import AiCare from '@/pages/AiCare'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />, // 공통 레이아웃
    children: [
      { index: true, element: <Home /> },
      { path: 'mypage', element: <MyPage /> },
      { path: 'recommend', element: <Recommend /> },
      { path: 'mentoring', element: <Mentoring /> },
      { path: 'guide', element: <Guide /> },
      { path: 'ai-care', element: <AiCare /> },
    ],
  },
])

import { createBrowserRouter } from 'react-router-dom'
import App from '@/App'
import Home from '@/pages/Home'
import MyPage from '@/pages/MyPage'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />, // 공통 레이아웃
    children: [
      { index: true, element: <Home /> },
      { path: 'mypage', element: <MyPage /> },
    ],
  },
])

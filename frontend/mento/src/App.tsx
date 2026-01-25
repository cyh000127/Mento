import { RouterProvider } from 'react-router-dom'
import { router } from './routes/router'

// App.tsx → Router만 담당
export default function App() {
  return <RouterProvider router={router} />
}
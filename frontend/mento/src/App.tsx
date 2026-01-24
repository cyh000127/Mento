import { Outlet } from 'react-router-dom'

export default function App() {
  return (
    <div>
      <header>MENTO Header</header>

      <main>
        <Outlet />
      </main>
    </div>
  )
}

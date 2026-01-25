import { Outlet } from 'react-router-dom'
import Header from '@/components/layouts/Header'
import Footer from '@/components/layouts/Footer'

export default function Layout() {

  return (
    <div className="min-h-screen flex flex-col">
      {/* Header */}
      <Header />

      {/* Main (남은 영역 채우기) */}
      <main className="flex-1">
        <div className="mx-auto max-w-[1200px] px-6 py-6">
          <Outlet />
        </div>
      </main>

      {/* Footer */}
      <Footer />
    </div>
  )
}

import { Outlet } from "react-router-dom"
import { Header } from "@/components/header"
import { Footer } from "@/components/footer"
import { useAuthStore } from "@/stores/useAuthStore"

export default function Layout() {
  const user = useAuthStore((state) => state.user)
  const isLoggedIn = !!user

  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <main className="flex-1">
        <Outlet />
      </main>
      {isLoggedIn && <Footer />}
    </div>
  )
}

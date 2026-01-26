import { useState } from "react"
import { Link } from "react-router-dom"
import { Bell, Package, Menu, X } from "lucide-react"
import { LoginModal } from "./login-modal"

const navItems = [
  { label: "추천", href: "/recommend" },
  { label: "멘토링", href: "/mentoring" },
  { label: "사용법", href: "/guide" },
  { label: "AI CARE", href: "/ai-care" },
]

export function Header() {
  const [isLoginOpen, setIsLoginOpen] = useState(false)
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)

  return (
    <>
      <header className="sticky top-0 z-50 h-14 bg-gradient-to-r from-primary-500 to-primary-400 shadow-sm">
        <div className="mx-auto flex h-full max-w-[1200px] items-center justify-between px-6">
          {/* Logo */}
          <Link 
            to="/" 
            className="text-xl font-bold tracking-tight text-dark-bg transition-opacity hover:opacity-80"
          >
            MENTO
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden items-center gap-8 md:flex">
            {navItems.map((item) => (
              <Link
                key={item.href}
                to={item.href}
                className="text-sm font-medium text-dark-bg/80 transition-colors hover:text-dark-bg"
              >
                {item.label}
              </Link>
            ))}
          </nav>

          {/* Right Actions */}
          <div className="flex items-center gap-3">
            <button
              type="button"
              className="hidden rounded-full p-2 text-dark-bg/80 transition-colors hover:bg-dark-bg/10 hover:text-dark-bg md:block"
              aria-label="알림"
            >
              <Bell className="h-5 w-5" />
            </button>
            <button
              type="button"
              className="hidden rounded-full p-2 text-dark-bg/80 transition-colors hover:bg-dark-bg/10 hover:text-dark-bg md:block"
              aria-label="인벤토리"
            >
              <Package className="h-5 w-5" />
            </button>
            <button
              type="button"
              onClick={() => setIsLoginOpen(true)}
              className="hidden rounded-full bg-dark-bg px-4 py-1.5 text-sm font-medium text-primary-500 transition-all hover:bg-dark-bg/90 md:block"
            >
              로그인
            </button>

            {/* Mobile Menu Toggle */}
            <button
              type="button"
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="rounded-full p-2 text-dark-bg/80 transition-colors hover:bg-dark-bg/10 hover:text-dark-bg md:hidden"
              aria-label="메뉴"
            >
              {isMobileMenuOpen ? (
                <X className="h-5 w-5" />
              ) : (
                <Menu className="h-5 w-5" />
              )}
            </button>
          </div>
        </div>

        {/* Mobile Menu */}
        {isMobileMenuOpen && (
          <div className="border-t border-dark-bg/10 bg-primary-400 md:hidden">
            <nav className="mx-auto max-w-[1200px] px-6 py-4">
              <div className="flex flex-col gap-3">
                {navItems.map((item) => (
                  <Link
                    key={item.href}
                    to={item.href}
                    onClick={() => setIsMobileMenuOpen(false)}
                    className="rounded-lg px-3 py-2 text-sm font-medium text-dark-bg/80 transition-colors hover:bg-dark-bg/10 hover:text-dark-bg"
                  >
                    {item.label}
                  </Link>
                ))}
                <div className="flex items-center gap-2 border-t border-dark-bg/10 pt-3">
                  <button
                    type="button"
                    className="rounded-full p-2 text-dark-bg/80 transition-colors hover:bg-dark-bg/10 hover:text-dark-bg"
                    aria-label="알림"
                  >
                    <Bell className="h-5 w-5" />
                  </button>
                  <button
                    type="button"
                    className="rounded-full p-2 text-dark-bg/80 transition-colors hover:bg-dark-bg/10 hover:text-dark-bg"
                    aria-label="인벤토리"
                  >
                    <Package className="h-5 w-5" />
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setIsLoginOpen(true)
                      setIsMobileMenuOpen(false)
                    }}
                    className="ml-auto rounded-full bg-dark-bg px-4 py-1.5 text-sm font-medium text-primary-500 transition-all hover:bg-dark-bg/90"
                  >
                    로그인
                  </button>
                </div>
              </div>
            </nav>
          </div>
        )}
      </header>

      <LoginModal isOpen={isLoginOpen} onClose={() => setIsLoginOpen(false)} />
    </>
  )
}

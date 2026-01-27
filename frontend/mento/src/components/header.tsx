import { useState } from "react"
import { Link } from "react-router-dom"
import { Bell, Package, Menu, X } from "lucide-react"
import { LoginModal } from "./login-modal"
import { NotificationModal, type Notification } from "./notification-modal"

const navItems = [
  { label: "추천", href: "/recommend" },
  { label: "멘토링", href: "/mentoring" },
  { label: "사용법", href: "/guide" },
  { label: "AI CARE", href: "/ai-care" },
]

// 목 데이터 (실제 서비스에서는 API로부터 가져옴)
const mockNotifications: Notification[] = [
  {
    id: "1",
    type: "expiration",
    message: "사용 중인 제품이 유통기한에 가까워지고 있습니다.",
    timestamp: new Date(Date.now() - 1000 * 60 * 15), // 15분 전
  },
  {
    id: "2",
    type: "consultation",
    message: "30분 후 상담이 시작됩니다.",
    timestamp: new Date(Date.now() - 1000 * 60 * 5), // 5분 전
  },
]

export function Header() {
  const [isLoginOpen, setIsLoginOpen] = useState(false)
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)
  const [isNotificationOpen, setIsNotificationOpen] = useState(false)
  const [notifications, setNotifications] = useState<Notification[]>(mockNotifications)

  const handleRemoveNotification = (id: string) => {
    setNotifications((prev) => prev.filter((notification) => notification.id !== id))
  }

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
            <div className="relative hidden md:block">
              <button
                type="button"
                onClick={() => setIsNotificationOpen(!isNotificationOpen)}
                className="rounded-full p-2 text-dark-bg/80 transition-colors hover:bg-dark-bg/10 hover:text-dark-bg"
                aria-label="알림"
              >
                <Bell className="h-5 w-5" />
                {notifications.length > 0 && (
                  <span className="absolute right-1 top-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white">
                    {notifications.length}
                  </span>
                )}
              </button>
              <NotificationModal
                isOpen={isNotificationOpen}
                onClose={() => setIsNotificationOpen(false)}
                notifications={notifications}
                onRemoveNotification={handleRemoveNotification}
              />
            </div>
            <Link
              to="/inventory"
              className="hidden rounded-full p-2 text-dark-bg/80 transition-colors hover:bg-dark-bg/10 hover:text-dark-bg md:block"
              aria-label="인벤토리"
            >
              <Package className="h-5 w-5" />
            </Link>
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
                  <div className="relative">
                    <button
                      type="button"
                      onClick={() => {
                        setIsNotificationOpen(!isNotificationOpen)
                        setIsMobileMenuOpen(false)
                      }}
                      className="rounded-full p-2 text-dark-bg/80 transition-colors hover:bg-dark-bg/10 hover:text-dark-bg"
                      aria-label="알림"
                    >
                      <Bell className="h-5 w-5" />
                      {notifications.length > 0 && (
                        <span className="absolute right-1 top-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white">
                          {notifications.length}
                        </span>
                      )}
                    </button>
                  </div>
                  <Link
                    to="/inventory"
                    onClick={() => setIsMobileMenuOpen(false)}
                    className="rounded-full p-2 text-dark-bg/80 transition-colors hover:bg-dark-bg/10 hover:text-dark-bg"
                    aria-label="인벤토리"
                  >
                    <Package className="h-5 w-5" />
                  </Link>
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

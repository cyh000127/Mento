import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Bell, Package, Menu, X } from "lucide-react";
import { LoginModal } from "./login-modal";
import { NotificationModal } from "./notification-modal";
import { ConfirmModal } from "@/components/common/confirm-modal";
import { useAuthStore } from "@/stores/useAuthStore";
import { authApi } from "@/api/authApi";

import { getNotificationEventSourceUrl, deleteNotification } from "@/api/notificationApi";
import type { NotificationResDto } from "@/types/notification";

const navItems = [
  // { label: "추천", href: "/recommend" }, // 260128 kjm 아직 구현 여부가 결정되지 않아서 주석 처리
  { label: "멘토링", href: "/mentoring" },
  { label: "사용법", href: "/guide" },
  { label: "AI CARE", href: "/ai-care" },
];

export function Header() {
  const navigate = useNavigate();
  const { isLoggedIn, user, accessToken, isAuthInitialized } = useAuthStore();
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [isLogoutConfirmOpen, setIsLogoutConfirmOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isNotificationOpen, setIsNotificationOpen] = useState(false);
  const [notifications, setNotifications] = useState<NotificationResDto[]>([]);

  // SSE 구독
  useEffect(() => {
    // 초기화가 완료되고, 로그인이 되어 있고, 토큰이 있어야 SSE 연결 시도
    if (!isAuthInitialized || !isLoggedIn || !accessToken) {
      setNotifications([]);
      return;
    }

    const url = getNotificationEventSourceUrl(accessToken);
    const eventSource = new EventSource(url);

    // 연결 성공 (디버깅용)
    eventSource.addEventListener("connect", (e: MessageEvent) => {
      console.log("SSE Connected:", e.data);
    });

    // 초기 알림 데이터 수신
    eventSource.addEventListener("initial-notifications", (e: MessageEvent) => {
      try {
        const data = JSON.parse(e.data) as NotificationResDto[];
        setNotifications(data);
      } catch (error) {
        console.error("Failed to parse initial notifications:", error);
      }
    });

    // 실시간 알림 수신
    eventSource.addEventListener("notification", (e: MessageEvent) => {
      try {
        const newNotification = JSON.parse(e.data) as NotificationResDto;
        setNotifications((prev) => [newNotification, ...prev]);

        // 브라우저 알림 요청
        if (Notification.permission === "granted") {
          new Notification("새로운 알림", { body: newNotification.content });
        }
      } catch (error) {
        console.error("Failed to parse notification:", error);
      }
    });

    eventSource.onerror = (e) => {
      console.error("SSE Error:", e);
      eventSource.close();
    };

    return () => {
      eventSource.close();
    };
  }, [isLoggedIn, accessToken, isAuthInitialized]);

  const handleRemoveNotification = async (id: number) => {
    try {
      await deleteNotification(id);
      setNotifications((prev) => prev.filter((n) => n.notificationId !== id));
    } catch (error) {
      console.error("Failed to delete notification:", error);
    }
  };

  const handleLogout = async () => {
    try {
      // authApi.logout()에서 이미 로컬 상태 초기화를 처리함
      await authApi.logout();
      navigate("/");
    } catch (error) {
      console.error("로그아웃 실패:", error);
      // authApi.logout()에서 실패해도 로컬 상태는 이미 초기화됨
      navigate("/");
    }
  };

  return (
    <>
      <header className="sticky top-0 z-50 bg-gradient-to-r from-primary-500 to-primary-400 shadow-sm">
        <div className="relative mx-auto flex h-14 max-w-[1200px] items-center px-6">
          {/* Left */}
          <div className="flex flex-1 items-center">
            <Link to="/" className="text-2xl font-bold tracking-tight text-dark-bg transition-opacity hover:opacity-80">
              MENTO
            </Link>
          </div>

          {/* Center (Desktop Nav only) */}
          <nav className="absolute left-1/2 hidden -translate-x-1/2 items-center gap-8 md:flex">
            {navItems.map((item) => (
              <Link key={item.href} to={item.href} className="text-base font-medium text-dark-bg/80 transition-colors hover:text-dark-bg">
                {item.label}
              </Link>
            ))}
          </nav>

          {/* Right */}
          <div className="flex flex-1 items-center justify-end gap-3">
            <div className="relative hidden md:block">
              <button type="button" onClick={() => setIsNotificationOpen(!isNotificationOpen)} className="rounded-full p-2 text-dark-bg/80 hover:bg-dark-bg/10">
                <Bell className="h-5 w-5" />
                {notifications.length > 0 && (
                  <span className="absolute right-1 top-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white">{notifications.length}</span>
                )}
              </button>
              <NotificationModal isOpen={isNotificationOpen} onClose={() => setIsNotificationOpen(false)} notifications={notifications} onRemoveNotification={handleRemoveNotification} />
            </div>

            <Link to="/inventory" className="hidden rounded-full p-2 text-dark-bg/80 hover:bg-dark-bg/10 md:block">
              <Package className="h-5 w-5" />
            </Link>

            {isLoggedIn ? (
              <div className="hidden items-center gap-3 md:flex">
                {user && (
                  <Link to="/mypage/consultations">
                    <span className="text-base font-medium text-dark-bg/90">{user.name}님</span>
                  </Link>
                )}
                <button onClick={() => setIsLogoutConfirmOpen(true)} className="rounded-full bg-dark-bg px-4 py-1.5 text-base text-primary-500">
                  로그아웃
                </button>
              </div>
            ) : (
              <button onClick={() => setIsLoginOpen(true)} className="hidden rounded-full bg-dark-bg px-4 py-1.5 text-base text-primary-500 md:block">
                로그인
              </button>
            )}

            {/* Mobile Toggle */}
            <button onClick={() => setIsMobileMenuOpen((v) => !v)} className="rounded-full p-2 text-dark-bg/80 hover:bg-dark-bg/10 md:hidden">
              {isMobileMenuOpen ? <X /> : <Menu />}
            </button>
          </div>
        </div>
      </header>

      {/* Mobile Menu */}
      {isMobileMenuOpen && (
        <div className="fixed top-14 z-40 w-full bg-primary-400 md:hidden">
          <nav className="mx-auto max-w-[1200px] px-6 py-4">
            <div className="flex flex-col gap-3">
              {navItems.map((item) => (
                <Link key={item.href} to={item.href} onClick={() => setIsMobileMenuOpen(false)} className="rounded-lg px-3 py-2 text-base font-medium text-dark-bg/80 hover:bg-dark-bg/10">
                  {item.label}
                </Link>
              ))}

              <div className="mt-3 flex items-center gap-2 border-t border-dark-bg/10 pt-3">
                <button
                  onClick={() => {
                    setIsNotificationOpen(true);
                    setIsMobileMenuOpen(false);
                  }}
                  className="rounded-full p-2 text-dark-bg/80 hover:bg-dark-bg/10"
                >
                  <Bell className="h-5 w-5" />
                </button>

                <Link to="/inventory" onClick={() => setIsMobileMenuOpen(false)} className="rounded-full p-2 text-dark-bg/80 hover:bg-dark-bg/10">
                  <Package className="h-5 w-5" />
                </Link>

                {isLoggedIn ? (
                  <div className="ml-auto flex items-center gap-2">
                    {user && <span className="text-base font-medium text-dark-bg/90">{user.name}님</span>}
                    <button
                      onClick={() => {
                        setIsLogoutConfirmOpen(true);
                        setIsMobileMenuOpen(false);
                      }}
                      className="rounded-full bg-dark-bg px-4 py-1.5 text-base text-primary-500"
                    >
                      로그아웃
                    </button>
                  </div>
                ) : (
                  <button
                    onClick={() => {
                      setIsLoginOpen(true);
                      setIsMobileMenuOpen(false);
                    }}
                    className="ml-auto rounded-full bg-dark-bg px-4 py-1.5 text-base text-primary-500"
                  >
                    로그인
                  </button>
                )}
              </div>
            </div>
          </nav>
        </div>
      )}

      <LoginModal isOpen={isLoginOpen} onClose={() => setIsLoginOpen(false)} />
      <ConfirmModal
        open={isLogoutConfirmOpen}
        onOpenChange={setIsLogoutConfirmOpen}
        onConfirm={() => {
          handleLogout();
          setIsLogoutConfirmOpen(false);
        }}
        title="로그아웃"
        message="로그아웃 하시겠습니까?"
        type="warning"
        confirmText="로그아웃"
        cancelText="취소"
      />
    </>
  );
}

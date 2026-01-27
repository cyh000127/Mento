import { useEffect, useRef } from "react"
import { useNavigate } from "react-router-dom"
import { X, Package, Video } from "lucide-react"

export type NotificationType = "expiration" | "consultation"

export interface Notification {
  id: string
  type: NotificationType
  message: string
  timestamp: Date
}

interface NotificationModalProps {
  isOpen: boolean
  onClose: () => void
  notifications: Notification[]
  onRemoveNotification: (id: string) => void
}

export function NotificationModal({
  isOpen,
  onClose,
  notifications,
  onRemoveNotification,
}: NotificationModalProps) {
  const modalRef = useRef<HTMLDivElement>(null)
  const navigate = useNavigate()

  // 모달 외부 클릭 시 닫기
  useEffect(() => {
    if (!isOpen) return

    const handleClickOutside = (event: MouseEvent) => {
      if (modalRef.current && !modalRef.current.contains(event.target as Node)) {
        onClose()
      }
    }

    document.addEventListener("mousedown", handleClickOutside)
    return () => document.removeEventListener("mousedown", handleClickOutside)
  }, [isOpen, onClose])

  // ESC 키로 닫기
  useEffect(() => {
    if (!isOpen) return

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose()
      }
    }

    document.addEventListener("keydown", handleEscape)
    return () => document.removeEventListener("keydown", handleEscape)
  }, [isOpen, onClose])

  const handleAction = (notification: Notification) => {
    if (notification.type === "expiration") {
      navigate("/inventory")
    } else if (notification.type === "consultation") {
      navigate("/consultation-room")
    }
    onClose()
  }

  const getNotificationIcon = (type: NotificationType) => {
    if (type === "expiration") {
      return <Package className="h-5 w-5 text-primary-500" />
    }
    return <Video className="h-5 w-5 text-primary-500" />
  }

  const getActionButtonText = (type: NotificationType) => {
    if (type === "expiration") {
      return (
        <>
          인벤토리
          <br />
          보기
        </>
      )
    }
    return (
      <>
        상담실
        <br />
        입장
      </>
    )
  }

  if (!isOpen) return null

  return (
    <div
      ref={modalRef}
      className="absolute right-0 top-full z-50 mt-2 w-[420px] animate-fade-in-down rounded-xl bg-white p-4 shadow-2xl"
      style={{
        animation: "fadeInDown 0.2s ease-out",
      }}
    >
      {/* 헤더 */}
      <div className="mb-4 flex items-center justify-between">
        <h3 className="text-lg font-semibold text-dark-bg">알림</h3>
        <button
          onClick={onClose}
          className="rounded-full p-1.5 text-gray-400 transition-colors hover:bg-gray-100 hover:text-gray-600"
          aria-label="닫기"
        >
          <X className="h-5 w-5" />
        </button>
      </div>

      {/* 알림 목록 */}
      <div className="space-y-3">
        {notifications.length === 0 ? (
          <div className="py-12 text-center">
            <p className="text-sm text-gray-400">알림이 없습니다.</p>
          </div>
        ) : (
          notifications.map((notification) => (
            <div
              key={notification.id}
              className="group relative rounded-lg border border-gray-100 bg-gradient-to-br from-gray-50 to-white p-4 transition-all hover:border-primary-200 hover:shadow-md"
            >
              <div className="flex items-center gap-3">
                {/* 아이콘 */}
                <div className="flex-shrink-0 rounded-full bg-primary-50 p-2">
                  {getNotificationIcon(notification.type)}
                </div>

                {/* 메시지 텍스트 */}
                <p className="flex-1 text-sm leading-relaxed text-gray-700">
                  {notification.message}
                </p>

                {/* CTA 버튼 */}
                <button
                  onClick={() => handleAction(notification)}
                  className="flex h-16 w-16 flex-shrink-0 items-center justify-center rounded-lg bg-gradient-to-r from-primary-500 to-primary-400 text-xs font-medium leading-tight text-white transition-all hover:shadow-md hover:brightness-105"
                >
                  {getActionButtonText(notification.type)}
                </button>

                {/* 개별 알림 닫기 버튼 */}
                <button
                  onClick={() => onRemoveNotification(notification.id)}
                  className="flex-shrink-0 rounded-full p-1 text-gray-300 opacity-0 transition-all hover:bg-gray-100 hover:text-gray-500 group-hover:opacity-100"
                  aria-label="알림 삭제"
                >
                  <X className="h-4 w-4" />
                </button>
              </div>

              {/* 타임스탬프 */}
              <div className="mt-2 text-right">
                <span className="text-xs text-gray-400">
                  {formatTimestamp(notification.timestamp)}
                </span>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

// 타임스탬프 포맷팅 유틸
function formatTimestamp(date: Date): string {
  const now = new Date()
  const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / 60000)

  if (diffInMinutes < 1) return "방금 전"
  if (diffInMinutes < 60) return `${diffInMinutes}분 전`
  if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}시간 전`
  return `${Math.floor(diffInMinutes / 1440)}일 전`
}

import { Link } from "react-router-dom"
import { CheckCircle2, CalendarDays, Clock, Droplets, Sparkles, Scissors } from "lucide-react"

type ConsultationCategory = "skincare" | "beauty" | "hair"

interface BookingData {
  category: ConsultationCategory | null
  date: Date | null
  time: string
}

interface BookingCompleteProps {
  bookingData: BookingData
}

const categoryLabels: Record<NonNullable<ConsultationCategory>, { label: string; icon: typeof Droplets }> = {
  skincare: { label: "스킨 케어", icon: Droplets },
  beauty: { label: "뷰티", icon: Sparkles },
  hair: { label: "헤어", icon: Scissors },
}

export function BookingComplete({ bookingData }: BookingCompleteProps) {
  const categoryInfo = bookingData.category ? categoryLabels[bookingData.category] : null
  const CategoryIcon = categoryInfo?.icon

  const formatDate = (date: Date | null) => {
    if (!date) return ""
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    const dayOfWeek = ["일", "월", "화", "수", "목", "금", "토"][date.getDay()]
    return `${year}년 ${month}월 ${day}일 (${dayOfWeek})`
  }

  return (
    <div className="flex min-h-[70vh] flex-col items-center justify-center">
      {/* Success Icon */}
      <div className="flex h-32 w-32 items-center justify-center rounded-full bg-gradient-to-br from-pastel-green-100 to-pastel-green-200 shadow-lg shadow-[#2ECC71]/20">
        <CheckCircle2 className="h-16 w-16 text-[#2ECC71]" strokeWidth={2} />
      </div>

      {/* Message */}
      <h1 className="mt-10 text-center text-2xl font-bold text-text-primary">
        상담 예약이 완료 되었습니다
      </h1>
      <p className="mt-3 text-center text-text-secondary">
        예약하신 일정에 맞춰 상담이 진행됩니다
      </p>

      {/* Booking Summary */}
      <div className="mt-10 w-full max-w-md rounded-2xl border border-border bg-card p-6 shadow-sm">
        <h3 className="mb-4 text-center font-semibold text-text-primary">예약 정보</h3>
        
        <div className="space-y-4">
          {/* Category */}
          {categoryInfo && CategoryIcon && (
            <div className="flex items-center gap-3 rounded-lg bg-muted/50 p-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary-100">
                <CategoryIcon className="h-5 w-5 text-primary-500" />
              </div>
              <div>
                <p className="text-xs text-text-secondary">상담 분야</p>
                <p className="font-medium text-text-primary">{categoryInfo.label}</p>
              </div>
            </div>
          )}

          {/* Date */}
          <div className="flex items-center gap-3 rounded-lg bg-muted/50 p-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary-100">
              <CalendarDays className="h-5 w-5 text-primary-500" />
            </div>
            <div>
              <p className="text-xs text-text-secondary">예약 날짜</p>
              <p className="font-medium text-text-primary">{formatDate(bookingData.date)}</p>
            </div>
          </div>

          {/* Time */}
          <div className="flex items-center gap-3 rounded-lg bg-muted/50 p-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary-100">
              <Clock className="h-5 w-5 text-primary-500" />
            </div>
            <div>
              <p className="text-xs text-text-secondary">예약 시간</p>
              <p className="font-medium text-text-primary">{bookingData.time}</p>
            </div>
          </div>
        </div>
      </div>

      {/* CTA Button */}
      <Link
        to="/mypage/consultations"
        className="mt-10 flex items-center gap-2 rounded-xl bg-primary-500 px-10 py-4 text-lg font-semibold text-dark-bg shadow-lg shadow-primary-500/30 transition-all hover:bg-primary-400"
      >
        나의 상담 예약 내역으로 가기
        <svg
          className="h-5 w-5"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={2}
        >
          <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
        </svg>
      </Link>

      {/* Secondary Link */}
      <Link
        to="/"
        className="mt-4 text-sm text-text-secondary transition-colors hover:text-text-primary"
      >
        홈으로 돌아가기
      </Link>
    </div>
  )
}

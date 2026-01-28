import { ArrowLeft, CalendarDays, Clock, Droplets, Sparkles, Scissors, CreditCard } from "lucide-react"
import type { ConsultationCategory } from "@/types/consultation"

interface PaymentProps {
  bookingData: {
    category: ConsultationCategory | null
    date: Date | null
    time: string
  }
  onPrev: () => void
  onPaymentComplete: () => void
}

const categoryLabels: Record<NonNullable<ConsultationCategory>, { label: string; icon: typeof Droplets; price: string }> = {
  skincare: { label: "스킨 케어", icon: Droplets, price: "30,000" },
  beauty: { label: "뷰티", icon: Sparkles, price: "35,000" },
  hair: { label: "헤어", icon: Scissors, price: "40,000" },
}

export function Payment({ bookingData, onPrev, onPaymentComplete }: PaymentProps) {
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

  const handlePayment = () => {
    // TODO: 실제 카카오페이 API 연동
    // 현재는 시뮬레이션으로 바로 완료 처리
    // 실제로는 카카오페이 결제창으로 리다이렉트 후, 결제 성공 콜백에서 onPaymentComplete 호출
    alert("카카오페이 결제 페이지로 이동합니다.\n(실제 결제 연동은 추후 구현 예정)")
    
    // 결제 성공 시 완료 화면으로 이동
    onPaymentComplete()
  }

  return (
    <div className="flex flex-col">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-text-primary">
          결제 정보를 확인해 주세요
        </h1>
        <p className="mt-2 text-text-secondary">
          결제 완료 후 상담 예약이 확정됩니다
        </p>
      </div>

      {/* Main Content */}
      <div className="mt-8 grid gap-6 lg:grid-cols-5">
        {/* Consultation Summary - Left Side (3 cols) */}
        <div className="lg:col-span-3">
          <div className="rounded-2xl border border-border bg-card p-6 shadow-sm">
            <h2 className="mb-6 text-lg font-bold text-text-primary">상담 예약 정보</h2>
            
            <div className="space-y-4">
              {/* Category */}
              {categoryInfo && CategoryIcon && (
                <div className="flex items-center justify-between rounded-xl bg-muted/50 p-4">
                  <div className="flex items-center gap-3">
                    <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary-100">
                      <CategoryIcon className="h-6 w-6 text-primary-500" />
                    </div>
                    <div>
                      <p className="text-xs text-text-secondary">상담 분야</p>
                      <p className="font-semibold text-text-primary">{categoryInfo.label}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-xs text-text-secondary">상담료</p>
                    <p className="text-lg font-bold text-text-primary">{categoryInfo.price}원</p>
                  </div>
                </div>
              )}

              {/* Date */}
              <div className="flex items-center gap-3 rounded-xl bg-muted/50 p-4">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary-100">
                  <CalendarDays className="h-6 w-6 text-primary-500" />
                </div>
                <div>
                  <p className="text-xs text-text-secondary">예약 날짜</p>
                  <p className="font-semibold text-text-primary">{formatDate(bookingData.date)}</p>
                </div>
              </div>

              {/* Time */}
              <div className="flex items-center gap-3 rounded-xl bg-muted/50 p-4">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary-100">
                  <Clock className="h-6 w-6 text-primary-500" />
                </div>
                <div>
                  <p className="text-xs text-text-secondary">예약 시간</p>
                  <p className="font-semibold text-text-primary">{bookingData.time}</p>
                </div>
              </div>
            </div>

            {/* Price Summary */}
            <div className="mt-6 space-y-3 border-t border-border pt-6">
              <div className="flex items-center justify-between text-text-secondary">
                <span>상담료</span>
                <span className="font-medium">{categoryInfo?.price}원</span>
              </div>
              <div className="flex items-center justify-between text-text-secondary">
                <span>할인</span>
                <span className="font-medium text-[#2ECC71]">- 0원</span>
              </div>
              <div className="flex items-center justify-between border-t border-border pt-3 text-lg font-bold text-text-primary">
                <span>총 결제 금액</span>
                <span className="text-primary-500">{categoryInfo?.price}원</span>
              </div>
            </div>
          </div>
        </div>

        {/* Payment Method - Right Side (2 cols) */}
        <div className="lg:col-span-2">
          <div className="rounded-2xl border border-border bg-card p-6 shadow-sm">
            <h2 className="mb-6 text-lg font-bold text-text-primary">결제 수단</h2>
            
            {/* KakaoPay Payment Option */}
            <button
              type="button"
              className="w-full rounded-xl border-2 border-primary-300 bg-gradient-to-br from-primary-50 to-primary-100 p-5 transition-all hover:border-primary-400 hover:shadow-md"
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary-200">
                    <CreditCard className="h-6 w-6 text-primary-600" />
                  </div>
                  <div className="text-left">
                    <p className="font-bold text-text-primary">카카오페이</p>
                    <p className="text-xs text-text-secondary">간편하고 안전한 결제</p>
                  </div>
                </div>
                <div className="flex h-6 w-6 items-center justify-center rounded-full bg-primary-500">
                  <svg
                    className="h-4 w-4 text-dark-bg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth={3}
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M5 13l4 4L19 7"
                    />
                  </svg>
                </div>
              </div>
            </button>

            {/* Payment Info Notice */}
            <div className="mt-6 rounded-lg bg-muted/50 p-4">
              <h3 className="mb-2 text-sm font-semibold text-text-primary">결제 안내</h3>
              <ul className="space-y-1 text-xs text-text-secondary">
                <li className="flex items-start gap-2">
                  <span className="mt-0.5 text-primary-500">•</span>
                  <span>결제 완료 후 예약이 확정됩니다</span>
                </li>
                <li className="flex items-start gap-2">
                  <span className="mt-0.5 text-primary-500">•</span>
                  <span>예약 변경 및 취소는 마이페이지에서 가능합니다</span>
                </li>
                <li className="flex items-start gap-2">
                  <span className="mt-0.5 text-primary-500">•</span>
                  <span>예약 시간 24시간 전까지 무료 취소가 가능합니다</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      {/* Navigation Buttons */}
      <div className="mt-12 flex items-center justify-between">
        <button
          type="button"
          onClick={onPrev}
          className="flex items-center gap-2 rounded-xl border border-border px-6 py-3 text-base font-semibold text-text-primary transition-colors hover:bg-muted"
        >
          <ArrowLeft className="h-5 w-5" />
          이전 단계
        </button>

        <button
          type="button"
          onClick={handlePayment}
          className="flex items-center gap-2 rounded-xl bg-primary-500 px-10 py-4 text-lg font-semibold text-dark-bg shadow-lg shadow-primary-500/30 transition-all hover:bg-primary-400"
        >
          <CreditCard className="h-5 w-5" />
          카카오페이로 결제하기
        </button>
      </div>
    </div>
  )
}

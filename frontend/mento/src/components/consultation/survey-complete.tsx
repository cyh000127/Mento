import { Check, CreditCard, CheckCircle2 } from "lucide-react"

interface SurveyCompleteProps {
  onNext: () => void
}

export function SurveyComplete({ onNext }: SurveyCompleteProps) {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center">
      {/* Success Icon */}
      <div className="flex h-32 w-32 items-center justify-center rounded-full bg-gradient-to-br from-pastel-purple-100 to-pastel-purple-200 shadow-lg shadow-[#e6e3fa]/20">
        <CheckCircle2 className="h-16 w-16 text-[#8B7CF5]" strokeWidth={2} />
      </div>

      {/* Message */}
      <h1 className="mt-10 text-center text-2xl font-bold text-text-primary">
        설문이 완료되었습니다
      </h1>
      <p className="mt-3 text-center text-lg text-text-secondary">
        결제를 완료해 주세요
      </p>

      {/* Additional Info */}
      <div className="mt-8 max-w-md rounded-xl border border-border bg-muted/50 p-6">
        <div className="flex items-start gap-4">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-primary-100">
            <CreditCard className="h-5 w-5 text-primary-500" />
          </div>
          <div>
            <h3 className="font-semibold text-text-primary">결제 안내</h3>
            <p className="mt-1 text-sm leading-relaxed text-text-secondary">
              결제 완료 후 상담 예약이 확정되며, 예약 내역은 마이페이지에서 확인하실 수 있습니다.
            </p>
          </div>
        </div>
      </div>

      {/* CTA Button */}
      <button
        type="button"
        onClick={onNext}
        className="mt-10 flex items-center gap-2 rounded-xl bg-primary-500 px-10 py-4 text-lg font-semibold text-dark-bg shadow-lg shadow-primary-500/30 transition-all hover:bg-primary-400"
      >
        결제하러 가기
        <svg
          className="h-5 w-5"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={2}
        >
          <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </div>
  )
}

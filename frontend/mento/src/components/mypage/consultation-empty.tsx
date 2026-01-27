import { CalendarX2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useNavigate } from "react-router-dom"

interface ConsultationEmptyProps {
  onBookConsultation?: () => void
}

export function ConsultationEmpty({ onBookConsultation }: ConsultationEmptyProps) {
  const navigate = useNavigate()

  const handleBookConsultation = () => {
    if (onBookConsultation) {
      onBookConsultation()
    }
    navigate("/consultation")
  }

  return (
    <div className="flex min-h-[400px] items-center justify-center rounded-xl border border-border bg-card p-12 shadow-sm">
      <div className="flex max-w-md flex-col items-center text-center">
        {/* Icon */}
        <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-muted">
          <CalendarX2 className="h-10 w-10 text-muted-foreground" />
        </div>

        {/* Title */}
        <h3 className="mb-3 text-xl font-bold text-foreground">
          상담 이력이 없습니다
        </h3>

        {/* Description */}
        <p className="mb-6 text-sm leading-relaxed text-muted-foreground">
          아직 예약된 상담이나 완료된 상담 이력이 없어요.
          <br />
          전문가와의 1:1 상담을 통해 맞춤형 케어를 받아보세요.
        </p>

        {/* CTA Button */}
        <Button
          onClick={handleBookConsultation}
          className="bg-primary-500 text-dark-bg hover:bg-primary-400"
        >
          상담 예약하기
        </Button>
      </div>
    </div>
  )
}

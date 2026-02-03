import { ArrowLeft } from "lucide-react"
import { Button } from "@/components/ui/button"
import type { Consultation, ConsultationStatus } from "@/types/consultation"

interface ConsultationDetailProps {
  consultation: Consultation
  onBack: () => void
  onGoToPayment?: (consultation: Consultation) => void
}

const statusLabels: Record<ConsultationStatus, string> = {
  pending: "결제 대기 중",
  scheduled: "예약 완료",
  completed: "상담 완료",
  cancelled: "예약 취소됨",
}

export function ConsultationDetail({
  consultation,
  onBack,
  onGoToPayment,
}: ConsultationDetailProps) {
  const formatDateTime = (dateStr: string, timeStr: string) => {
    return `${dateStr.replace(/-/g, ".")} ${timeStr}`
  }
  const surveyItems =
    consultation.surveyInfo?.surveys ?? consultation.preConsultationQA

  return (
    <div className="bg-background py-8">
      <div className="mx-auto px-6">
        {/* Main Card */}
        <div className="space-y-6">
          {/* Header with Back Button */}
          <div className="flex items-center justify-between border-b border-border pb-3">
            <h1 className="text-2xl font-bold text-foreground">
              상담 관리
            </h1>
            <Button
              onClick={onBack}
              variant="ghost"
              className="text-muted-foreground hover:text-foreground"
            >
              <ArrowLeft className="mr-2 h-4 w-4" />
              목록으로
            </Button>
          </div>

          {/* Consultation Info */}
          <div className="space-y-4">
            {/* Date Time Row */}
            <div className="flex items-start gap-20">
              <span className="text-sm font-medium text-foreground min-w-[80px]">상담 일자</span>
              <span className="text-sm text-foreground">
                {formatDateTime(consultation.scheduledDate, consultation.scheduledTime)}
              </span>
            </div>

            {/* Status Row */}
            <div className="flex items-start gap-20">
              <span className="text-sm font-medium text-foreground min-w-[80px]">상태</span>
              <div className="flex items-center gap-4">
                <span className="text-sm text-foreground">
                  {statusLabels[consultation.status]}
                </span>
                {consultation.status === "pending" && onGoToPayment && (
                  <Button 
                    size="sm" 
                    onClick={() => onGoToPayment(consultation)} 
                    className="bg-primary-500 text-white hover:bg-primary-600"
                  >
                    결제하기
                  </Button>
                )}
              </div>
            </div>

            {/* Pre-Consultation Q&A */}
            {surveyItems && surveyItems.length > 0 && (
              <div className="flex items-start gap-20">
                <span className="text-sm font-medium text-foreground min-w-[80px]">사전정보</span>
                <div className="flex-1 space-y-4">
                  {surveyItems.map((qa, index) => (
                    <div key={index} className="bg-primary-100 rounded-lg p-4 space-y-2">
                      <div>
                        <p className="text-sm font-medium text-foreground mb-1">질문</p>
                        <p className="text-sm text-muted-foreground">{qa.question}</p>
                      </div>
                      <div>
                        <p className="text-sm font-medium text-foreground mb-1">답변</p>
                        <p className="text-sm text-muted-foreground">{qa.answer}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

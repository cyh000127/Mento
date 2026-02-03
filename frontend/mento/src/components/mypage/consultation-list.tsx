import { ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import type { Consultation } from "@/types/consultation";

interface ConsultationListProps {
  consultations: Consultation[];
  onViewDetail: (consultation: Consultation) => void;
  onCancelConsultation: (consultationId: string) => void;
  onEnterRoom: (roomUrl: string) => void;
  onGoToPayment: (consultation: Consultation) => void;
}

export function ConsultationList({ consultations, onViewDetail, onCancelConsultation, onEnterRoom, onGoToPayment }: ConsultationListProps) {
  const formatDateTime = (dateStr: string, timeStr: string) => {
    return `${dateStr.replace(/-/g, ".")} ${timeStr}`;
  };

  const isToday = (dateStr: string) => {
    return new Date(dateStr).toDateString() === new Date().toDateString();
  };

  return (
    <div className="rounded-xl border border-border bg-card shadow-sm overflow-hidden">
      {/* Table Header */}
      <div className="text-center grid grid-cols-12 gap-4 bg-muted px-6 py-4 text-sm font-semibold text-foreground border-b border-border">
        <div className="col-span-3">상담 일자</div>
        <div className="col-span-2">유형</div>
        <div className="col-span-3">예약 사전 정보 확인</div>
        <div className="col-span-4">상태</div>
      </div>

      {/* Table Body */}
      <div className="divide-y divide-border">
        {consultations.map((consultation) => (
          <div key={consultation.id} className="grid grid-cols-12 gap-4 px-6 py-5 hover:bg-muted/30 transition-colors">
            {/* Date Time */}
            <div className="col-span-3 flex items-center text-sm text-foreground justify-center">{formatDateTime(consultation.scheduledDate, consultation.scheduledTime)}</div>

            {/* Category */}
            <div className="col-span-2 flex items-center justify-center">
              <span className="text-sm text-foreground">{consultation.mentorTypeName}</span>
            </div>

            {/* Pre-consultation Info Link */}
            <div className="col-span-3 flex items-center justify-center">
              <button onClick={() => onViewDetail(consultation)} className="flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground transition-colors">
                입력한 사전 정보 보러가기
                <ChevronRight className="h-4 w-4" />
              </button>
            </div>

            {/* Status Buttons */}
            <div className="col-span-4 flex items-center gap-2 justify-center">
              {consultation.status === "pending" && (
                <>
                  <Button size="sm" variant="outline" className="bg-yellow-100 text-yellow-800 hover:bg-yellow-200 border-yellow-300">
                    결제 대기 중
                  </Button>
                  <Button size="sm" onClick={() => onGoToPayment(consultation)} className="bg-primary-500 text-white hover:bg-primary-600">
                    결제하기
                  </Button>
                </>
              )}
              {consultation.status === "scheduled" && (
                <>
                  <Button size="sm" variant="outline" className="bg-muted text-foreground hover:bg-muted/80">
                    예약 완료
                  </Button>
                  {isToday(consultation.scheduledDate) && consultation.roomUrl && (
                    <Button size="sm" onClick={() => onEnterRoom(consultation.roomUrl!)} className="bg-muted text-foreground hover:bg-muted/80">
                      상담 방 이동
                    </Button>
                  )}
                  <Button size="sm" variant="outline" onClick={() => onCancelConsultation(consultation.id)} className="bg-muted text-foreground hover:bg-muted/80">
                    예약 취소
                  </Button>
                </>
              )}
              {consultation.status === "completed" && (
                <Button size="sm" variant="outline" className="bg-muted text-foreground hover:bg-muted/80">
                  상담 완료
                </Button>
              )}
              {consultation.status === "cancelled" && (
                <Button size="sm" variant="outline" className="bg-muted text-muted-foreground" disabled>
                  예약 취소됨
                </Button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

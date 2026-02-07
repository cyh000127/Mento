import { Button } from "@/components/ui/button";
import type { Consultation } from "@/types/consultation";
import { ChevronRight } from "lucide-react";

interface ConsultationListProps {
  consultations: Consultation[];
  onViewDetail: (consultation: Consultation) => void;
  onEnterRoom: (reservationId: number) => void;
  // 시연 끝나고 살리기 kjm 2026-02-06
  // onEnterRoom: (consultation: Consultation) => void; 
  onGoToPayment: (consultation: Consultation) => void;
  onViewReport: (consultation: Consultation) => void;
}

export function ConsultationList({ consultations, onViewDetail, onEnterRoom, onGoToPayment, onViewReport }: ConsultationListProps) {
  const formatDateTime = (dateStr: string, timeStr: string) => {
    return `${dateStr.replace(/-/g, ".")} ${timeStr}`;
  };

  const handleViewReport = (consultation: Consultation) => {
    onViewReport(consultation);
  };

  return (
    <div className="rounded-xl border border-border bg-card shadow-sm overflow-hidden">
      {/* Table Header */}
      <div className="text-center grid grid-cols-12 gap-4 bg-muted px-6 py-4 text-sm font-semibold text-foreground border-b border-border">
        <div className="col-span-2">상담 일자</div>
        <div className="col-span-2">유형</div>
        <div className="col-span-3">상태</div>
        <div className="col-span-3">AI 리포트</div>
        <div className="col-span-2">상세</div>
      </div>

      {/* Table Body */}
      <div className="divide-y divide-border">
        {consultations.map((consultation) => (
          <div key={consultation.id} className="grid grid-cols-12 gap-4 px-6 py-5 hover:bg-muted/30 transition-colors">
            {/* Date Time */}
            <div className="col-span-2 flex items-center text-sm text-foreground justify-center">{formatDateTime(consultation.scheduledDate, consultation.scheduledTime)}</div>

            {/* Category */}
            <div className="col-span-2 flex items-center justify-center">
              <span className="text-sm text-foreground">{consultation.mentorTypeName}</span>
            </div>

            {/* Status Buttons */}
            <div className="col-span-3 flex items-center gap-2 justify-center">
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
                  <Button size="sm" variant="outline" className="bg-muted text-foreground hover:bg-muted/80" disabled>
                    예약 완료
                  </Button>
                  {/* 시연 끝나고 살리기 kjm 2026-02-06 */}
                  {/* <Button size="sm" onClick={() => onEnterRoom(consultation)} className="bg-muted text-foreground hover:bg-muted/80" disabled={!consultation.reservationId}> */}
                  <Button size="sm" onClick={() => onEnterRoom(consultation.reservationId!)} className="bg-muted text-foreground hover:bg-muted/80" disabled={!consultation.reservationId}>
                    상담방 이동
                  </Button>
                </>
              )}
              {consultation.status === "completed" && (
                <Button size="sm" variant="outline" className="bg-muted text-foreground hover:bg-muted/80">
                  상담 완료
                </Button>
              )}
            </div>

            {/* ai 리포트 */}
            <div className="col-span-3 flex items-center justify-center">
              <Button onClick={() => handleViewReport(consultation)} className="h-[36px] bg-muted text-foreground hover:bg-muted/80">
                보러 가기
              </Button>
            </div>

            {/* Pre-consultation Info Link */}
            <div className="col-span-2 flex items-center justify-center">
              <button onClick={() => onViewDetail(consultation)} className="flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground transition-colors">
                자세히 보기
                <ChevronRight className="h-4 w-4" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

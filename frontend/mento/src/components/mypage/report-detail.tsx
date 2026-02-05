import { ConsultationReport } from "@/components/consultation/ConsultationReport";
import { consultationReportMock } from "@/components/consultation/consultationReportMock";
import type { Consultation } from "@/types/consultation";

interface ReportDetailProps {
  report: Consultation["report"];
}

export function ReportDetail({ report }: ReportDetailProps) {
  return (
    <div className="flex items-start gap-20">
      <div className="flex-1">
        <ConsultationReport report={report ?? consultationReportMock} />
        {/* {report ? (
          <ConsultationReport report={report} />
        ) : (
          <div className="flex items-center justify-center py-20 bg-muted/30 rounded-lg border border-dashed border-border">
            <p className="text-muted-foreground">상담이 끝나고 생성이 완료되면 리포트를 확인할 수 있습니다</p>
          </div>
        )} */}
      </div>
    </div>
  );
}

import { ConsultationReport } from "@/components/consultation/ConsultationReport";
import { useConsultationReportStore } from "@/stores/useConsultationReportStore";
import type { ReportData } from "@/components/consultation/ConsultationReport";

export function ReportDetail() {
  const reportData = useConsultationReportStore((state) => state.report);

  if (!reportData) {
    return (
      <div className="flex items-center justify-center py-20 bg-muted/30 rounded-lg border border-dashed border-border">
        <p className="text-muted-foreground">상담이 끝나고 생성이 완료되면 리포트를 확인할 수 있습니다</p>
      </div>
    );
  }

  let parsedReport: ReportData | undefined;

  try {
    parsedReport = reportData.content ? (JSON.parse(reportData.content) as ReportData) : undefined;
  } catch (error) {
    console.error("상담 리포트 JSON 파싱 실패:", error);
    parsedReport = undefined;
  }

  return (
    <div className="flex items-start gap-20">
      <div className="flex-1">
        <ConsultationReport report={parsedReport} />
      </div>
    </div>
  );
}

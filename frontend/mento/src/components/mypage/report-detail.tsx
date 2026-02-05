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
      </div>
    </div>
  );
}

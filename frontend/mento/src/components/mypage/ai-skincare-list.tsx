import { ChevronRight } from "lucide-react";
import type { AiSkincareDiagnosis } from "@/types/ai-skincare";

interface AiSkincareListProps {
  diagnoses: AiSkincareDiagnosis[];
  onViewDetail: (diagnosis: AiSkincareDiagnosis) => void;
}

export function AiSkincareList({ diagnoses, onViewDetail }: AiSkincareListProps) {
  return (
    <div className="rounded-xl border border-border bg-card shadow-sm overflow-hidden">
      {/* Table Header */}
      <div className="text-center grid grid-cols-12 gap-4 bg-muted px-6 py-4 text-sm font-semibold text-foreground border-b border-border">
        <div className="col-span-4">진단 일자</div>
        <div className="col-span-8">상세</div>
      </div>

      {/* Table Body */}
      <div className="divide-y divide-border">
        {diagnoses.map((diagnosis, index) => (
          <div key={diagnosis.id} className="grid grid-cols-12 gap-4 px-6 py-5 hover:bg-muted/30 transition-colors">
            {/* Diagnosis Date */}
            <div className="col-span-4 flex items-center justify-center text-sm text-foreground">{diagnosis.created_at.slice(0, 10).replace(/-/g, ".")}</div>

            {/* View Detail Link */}
            <div className="col-span-8 flex items-center justify-center">
              <button onClick={() => onViewDetail(diagnosis)} className="flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground transition-colors">
                {100 - diagnosis.total_score}점 · {diagnosis.skin_type_summary} / 상세보기
                <ChevronRight className="h-4 w-4" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

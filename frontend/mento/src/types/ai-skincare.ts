import type { PeriodFilter } from "./consultation";

export type { PeriodFilter };

// AI 피부 진단 내역
export interface AiSkincareDiagnosis {
  id: number;
  created_at: string;
  total_score: number;
  skin_type_summary: string;
}

// 필터 전용 타입
export interface AiSkincareFilters {
  period: PeriodFilter;
  startDate?: string;
  endDate?: string;
}

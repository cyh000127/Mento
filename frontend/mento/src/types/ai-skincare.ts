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

// AI 피부 진단 상세 정보
export interface SkinAnalysisDetail {
  score: number;
  grade: number;
  raw_value: number;
  description: string;
}

export interface SkinAnalysisDetailData {
  total_score: number;
  total_grade: number;
  skin_type_summary: string;
  details: Record<string, SkinAnalysisDetail>;
}

import type { PeriodFilter } from "./consultation"

export type { PeriodFilter }

// AI 피부 진단 내역
export interface AiSkincareDiagnosis {
  id: string
  diagnosisDate: string // ISO format (YYYY-MM-DD)
  skinType?: string
  concerns?: string[]
  recommendations?: string[]
  imageUrl?: string
}

// 필터 전용 타입
export interface AiSkincareFilters {
  period: PeriodFilter
  startDate?: string
  endDate?: string
}

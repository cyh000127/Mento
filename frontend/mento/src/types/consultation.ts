export type ConsultationCategory = "skincare" | "beauty" | "hair" // 순수 도메인 카테고리 

export type ConsultationCategoryValue = ConsultationCategory | null  //UI / 선택 상태용 카테고리

export type ConsultationStatus = "scheduled" | "completed" | "cancelled"

export type PeriodFilter = "1month" | "3months" | "6months" | "12months"

// 실제 상담 도메인 모델
export interface Consultation {
  id: string
  category?: ConsultationCategory
  scheduledDate: string // ISO format
  scheduledTime: string // e.g., "14:00"
  status: ConsultationStatus
  mentorTypeName?: string
  expertName?: string
  expertTitle?: string
  preConsultationQA?: PreConsultationQA[]
  roomUrl?: string
  memo?: string
}

export interface PreConsultationQA {
  question: string
  answer: string
}

// 필터 전용 타입 (UI 관점)
export interface ConsultationFilters {
  period: PeriodFilter
  startDate?: string
  endDate?: string
  category: ConsultationCategory | "all"
}

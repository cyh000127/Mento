export type ConsultationCategory = "skincare" | "beauty" | "hair" | "general"; // 순수 도메인 카테고리

export type ConsultationCategoryValue = ConsultationCategory | null; //UI / 선택 상태용 카테고리

export type ConsultationStatus = "pending" | "scheduled" | "completed" | "cancelled";

export type PeriodFilter = "1month" | "3months" | "6months" | "12months";

// 실제 상담 도메인 모델
export interface Consultation {
  id: string;
  category?: ConsultationCategory;
  scheduledDate: string; // ISO format
  scheduledTime: string; // e.g., "14:00"
  status: ConsultationStatus;
  mentorTypeName?: string;
  expertName?: string;
  expertTitle?: string;
  preConsultationQA?: PreConsultationQA[];
  surveyInfo?: ConsultationSurveyInfo;
  roomUrl?: string;
  memo?: string;
  reservationId?: number;
  reportId?: number;
  paymentId?: number;
  report?: {
    sessions?: {
      sessionId?: string;
      sessionTitle?: string;
      subSections?: {
        subTitle?: string;
        descriptions?: string[];
      }[];
    }[];
  };
}

export interface PreConsultationQA {
  question: string;
  answer: string;
}

export interface ConsultationSurveyInfo {
  surveys: PreConsultationQA[];
}

// 필터 전용 타입 (UI 관점)
export interface ConsultationFilters {
  period: PeriodFilter;
  startDate?: string;
  endDate?: string;
  category: ConsultationCategory | "all";
}

// 상담 세션 생성 API 관련 타입
export type ParticipantRole = "MENTOR" | "USER";

export interface ConsultationSessionData {
  timetableId: number;
  roomToken: string;
  roomName: string;
  livekitUrl: string;
  participantRole: ParticipantRole;
  enteredAt: string;
}

export interface ConsultationSessionResponse {
  success: boolean;
  data: ConsultationSessionData;
  error: null | { message: string };
  timestamp: string;
}

export interface ConsultationReportData {
  reportId: number;
  content: string; // JSON String
  mediaUrls: string[] | null;
}

export interface ConsultationReportResponse {
  success: boolean;
  data: ConsultationReportData;
  error: null | { message: string };
  timestamp: string;
}

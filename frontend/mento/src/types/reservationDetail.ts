export interface ReservationDetailUserInfo {
  id: number;
  name?: string;
  userName?: string;
}

export interface ReservationDetailMentorInfo {
  id?: number;
  name?: string;
  mentorId?: number;
  mentorName?: string;
}

export interface ReservationDetailMentorTypeInfo {
  id?: number;
  name?: string;
  mentorTypeId?: number;
  mentorTypeName?: string;
  mentorTypeDescription?: string;
  price?: number;
}

export interface ReservationDetailSurveyItem {
  question: string;
  answer: string;
}

export interface ReservationDetailSurveyInfo {
  surveys?: ReservationDetailSurveyItem[];
}

export interface ReservationDetailData {
  reservationId: number;
  reportId: number;
  userInfo: ReservationDetailUserInfo;
  mentorInfo?: ReservationDetailMentorInfo;
  mentorTypeInfo?: ReservationDetailMentorTypeInfo;
  timetableId: number;
  scheduledDate: string;
  scheduledTime: string;
  reservationStatus: string;
  createdAt: string;
  updatedAt: string;
  surveyData?: string;
  surveyInfo?: ReservationDetailSurveyInfo;
}

export interface ReservationDetailError {
  status: string;
  message: string;
  method: string;
  requestUri: string;
  errors: unknown[];
}

export interface ReservationDetailResponse {
  success: boolean;
  data: ReservationDetailData | null;
  error: ReservationDetailError | null;
  timestamp: string;
}

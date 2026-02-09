export interface ReservationSurveyRequest {
  surveyData: string
}

export interface ReservationSurveyUserInfo {
  userId: number
  userName: string
}

export interface ReservationSurveyMentorInfo {
  mentorId: number
  mentorName: string
}

export interface ReservationSurveyMentorTypeInfo {
  mentorTypeId: number
  mentorTypeName: string
  mentorTypeDescription: string
}

export interface ReservationSurveyData {
  reservationId: number
  userInfo: ReservationSurveyUserInfo
  mentorInfo: ReservationSurveyMentorInfo
  mentorTypeInfo: ReservationSurveyMentorTypeInfo
  timetableId: number
  scheduledDate: string
  scheduledTime: string
  reservationStatus: string
  createdAt: string
  updatedAt: string
}

export interface ReservationSurveyError {
  status: string
  message: string
  method: string
  requestUri: string
  errors: unknown[]
}

export interface ReservationSurveyResponse {
  success: boolean
  data: ReservationSurveyData | null
  error: ReservationSurveyError | null
  timestamp: string
}

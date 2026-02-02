export type ReservationStatus = string

export interface ReservationListParams {
  startDate: string
  endDate: string
  status?: ReservationStatus
  page?: number
  size?: number
}

export interface ReservationMentorType {
  id: number
  name: string
  description: string
}

export interface ReservationListItem {
  reservationId: number
  scheduledDate: string
  mentorType: ReservationMentorType
  status: ReservationStatus
}

export interface ReservationListData {
  content: ReservationListItem[]
  hasNext: boolean
  totalPages: number
  totalElements: number
  page: number
  size: number
  isFirst: boolean
  isLast: boolean
}

export interface ReservationListError {
  status: string
  message: string
  method: string
  requestUri: string
  errors: unknown[]
}

export interface ReservationListResponse {
  success: boolean
  data: ReservationListData | null
  error: ReservationListError | null
  timestamp: string
}

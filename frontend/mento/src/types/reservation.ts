export interface ReservationDraftRequest {
  slotId: number
}

export interface ReservationDraftSlotInfo {
  timetableId: number
  slotId: number
  scheduledTime: string
  price: number
  maxCapacity: number
  currentCapacity: number
  availableCapacity: number
  status: string
}

export interface ReservationDraftData {
  reservationId: number
  timetableSlotInfoDto: ReservationDraftSlotInfo
  status: "DRAFT"
  expiresAt: string
}

export interface ReservationDraftError {
  status: string
  message: string
  method: string
  requestUri: string
  errors: unknown[]
}

export interface ReservationDraftResponse {
  success: boolean
  data: ReservationDraftData | null
  error: ReservationDraftError | null
  timestamp: string
}

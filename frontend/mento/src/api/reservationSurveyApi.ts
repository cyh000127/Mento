import { api } from "./axios"
import type { ReservationSurveyRequest, ReservationSurveyResponse, ReservationSurveyData } from "@/types/reservationSurvey"

export async function updateReservationSurvey(
  reservationId: number,
  payload: ReservationSurveyRequest
): Promise<ReservationSurveyData> {
  const response = await api.put<ReservationSurveyResponse>(`/reservations/${reservationId}/survey`, payload)
  if (!response.data.success || !response.data.data) {
    throw new Error(response.data.error?.message ?? "설문 저장에 실패했습니다.")
  }
  return response.data.data
}

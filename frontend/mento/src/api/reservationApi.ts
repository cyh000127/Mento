import { api } from "./axios"
import type { ReservationDraftData, ReservationDraftRequest, ReservationDraftResponse } from "@/types/reservation"
import type {
  ReservationListData,
  ReservationListParams,
  ReservationListResponse,
} from "@/types/reservationList"

export async function createReservationDraft(
  payload: ReservationDraftRequest
): Promise<ReservationDraftData> {
  const response = await api.post<ReservationDraftResponse>("/reservations/draft", payload)
  return response.data.data as ReservationDraftData
}

export async function getReservationList(
  params: ReservationListParams
): Promise<ReservationListData> {
  const response = await api.get<ReservationListResponse>("/reservations", {
    params,
  })

  if (!response.data.data) {
    throw new Error(response.data.error?.message ?? "예약 목록 조회에 실패했습니다.")
  }

  return response.data.data
}

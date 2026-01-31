import { api } from "./axios"
import type { ReservationDraftData, ReservationDraftRequest, ReservationDraftResponse } from "@/types/reservation"

export async function createReservationDraft(
  payload: ReservationDraftRequest
): Promise<ReservationDraftData> {
  const response = await api.post<ReservationDraftResponse>("/reservations/draft", payload)
  return response.data.data as ReservationDraftData
}

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
  params?: ReservationListParams
): Promise<ReservationListData> {
  const response = await api.get<ReservationListResponse | ReservationListData>(
    "/reservations",
    params ? { params } : undefined
  )

  const payload = "data" in response.data ? response.data.data : response.data

  if (!payload) {
    const errorMessage =
      "data" in response.data
        ? response.data.error?.message
        : undefined

    throw new Error(errorMessage ?? "예약 목록 조회에 실패했습니다.")
  }

  const normalizeScheduledDateTime = (scheduledDate: string, scheduledTime?: string) => {
    if (scheduledTime) {
      const dateOnly = scheduledDate.includes("T")
        ? scheduledDate.split("T")[0]
        : scheduledDate.includes(" ")
          ? scheduledDate.split(" ")[0]
          : scheduledDate

      return { scheduledDate: dateOnly, scheduledTime }
    }

    if (scheduledDate.includes("T")) {
      const [datePart, timePart] = scheduledDate.split("T")
      return { scheduledDate: datePart, scheduledTime: timePart.slice(0, 5) }
    }

    if (scheduledDate.includes(" ")) {
      const [datePart, timePart] = scheduledDate.split(" ")
      return { scheduledDate: datePart, scheduledTime: timePart.slice(0, 5) }
    }

    return { scheduledDate, scheduledTime: "00:00" }
  }

  const normalizedPayload: ReservationListData = {
    ...payload,
    content: payload.content.map((item) => {
      const normalized = normalizeScheduledDateTime(
        item.scheduledDate,
        item.scheduledTime
      )

      return {
        ...item,
        scheduledDate: normalized.scheduledDate,
        scheduledTime: normalized.scheduledTime,
      }
    }),
  }

  return normalizedPayload
}

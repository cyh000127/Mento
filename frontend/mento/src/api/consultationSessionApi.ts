// import { api } from "./axios";
import { testApi } from "./axios";
import type { ConsultationSessionData, ConsultationSessionResponse } from "@/types/consultation";

/**
 * 상담 세션 생성 API
 * POST /api/v1/reservations/{id}/sessions
 *
 * @description
 * 예약 기반 실제 상담 세션을 생성합니다.
 * LiveKit 접속 자격증명(roomToken, roomName, livekitUrl)을 발급받습니다.
 *
 * @param reservationId - 예약 ID
 * @returns 세션 생성 결과 (LiveKit 접속 정보 포함)
 *
 * @throws {Error} API 호출 실패 시
 */
export async function createConsultationSession(reservationId: number): Promise<ConsultationSessionData> {
  console.log("📡 API 호출 시작:", {
    url: `/reservations/${reservationId}/sessions`,
    method: "POST",
    baseURL: testApi,
  });

  try {
    const response = await testApi.post<ConsultationSessionResponse>(`/reservations/${reservationId}/sessions`);

    console.log("✅ API 응답 성공:", response.data);

    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.error?.message || "상담 세션 생성에 실패했습니다.");
    }

    return response.data.data;
  } catch (error) {
    console.error("❌ API 호출 실패:", {
      error,
      message: error instanceof Error ? error.message : "Unknown error",
      response: (error as any)?.response?.data,
    });
    throw error;
  }
}

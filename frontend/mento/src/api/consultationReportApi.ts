
import { api } from "./axios";
import type { ConsultationReportData, ConsultationReportResponse } from "@/types/consultation";

/**
 * 상담 보고서 요약본 상세 조회 API
 * GET /api/v1/consulting/{id}
 *
 * @description
 * 상담 완료 후 생성된 AI 보고서의 상세 내용을 조회합니다.
 * content는 JSON 문자열 형태이며, 호출부에서 파싱하여 사용해야 합니다.
 *
 * @param reportId - 상담 보고서 ID
 * @returns 상담 보고서 상세 데이터 (reportId, content, mediaUrls)
 *
 * @throws {Error} API 호출 실패 시
 */
export async function getConsultingReportDetail(reportId: number | string): Promise<ConsultationReportData> {
  try {
    const response = await api.get<ConsultationReportResponse>(`/consulting/${reportId}`);

    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.error?.message || "상담 보고서 조회에 실패했습니다.");
    }

    // content(JSON) 파싱 없이 그대로 반환 (요구사항)
    return response.data.data;
  } catch (error) {
    console.error("상담 보고서 상세 조회 실패:", {
      error,
      message: error instanceof Error ? error.message : "Unknown error",
      response: (error as any)?.response?.data,
    });
    throw error;
  }
}

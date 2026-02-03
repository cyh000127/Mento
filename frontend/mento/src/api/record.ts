import { api } from "./axios";
import type { EgressRequestPayload, EgressResponse, RecordApiResponse } from "@/types/record";

/**
 * 녹화 시작 API
 * POST {API_ORIGIN}/api/v1/egress/start
 */
export const startRecording = async (payload: EgressRequestPayload): Promise<EgressResponse> => {
  try {
    const response = await api.post<RecordApiResponse<EgressResponse>>("/egress/start", payload);

    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.error?.message || "녹화 시작에 실패했습니다.");
    }

    return response.data.data;
  } catch (error) {
    console.error("녹화 시작 호출 실패:", error);
    throw error;
  }
};

/**
 * 녹화 종료 API
 * POST {API_ORIGIN}/api/v1/egress/stop
 */
export const stopRecording = async (roomId: string): Promise<void> => {
  try {
    const response = await api.post<RecordApiResponse<null>>("/egress/stop", { roomId });

    if (!response.data.success) {
      throw new Error(response.data.error?.message || "녹화 종료에 실패했습니다.");
    }
  } catch (error) {
    console.error("녹화 종료 호출 실패:", error);
    throw error;
  }
};

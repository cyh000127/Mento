import { api } from "./axios";
import type { LivekitTestTokenResponse, LivekitTestTokenParams } from "@/types/livekitTest";

/**
 * LiveKit 테스트 토큰 발급 API (인증 없음)
 * GET /api/v1/test/livekit/token
 *
 * @description
 * 로그인 및 예약 플로우와 완전히 분리된 테스트 전용 API입니다.
 * 인증이 필요하지 않으며, 쿼리 파라미터로 roomName, userName, role을 전달합니다.
 *
 * @example
 * // MENTOR 역할로 test-room 입장
 * const token = await fetchLivekitTestToken({
 *   roomName: "test-room",
 *   userName: "mentor-1",
 *   role: "MENTOR"
 * });
 *
 * // USER 역할로 test-room 입장 (같은 roomName 사용)
 * const token = await fetchLivekitTestToken({
 *   roomName: "test-room",
 *   userName: "user-1",
 *   role: "USER"
 * });
 */
export const fetchLivekitTestToken = async (params?: LivekitTestTokenParams): Promise<LivekitTestTokenResponse> => {
  // 쿼리 파라미터 구성 (기본값 적용)
  const queryParams = new URLSearchParams({
    roomName: params?.roomName || "test-room",
    userName: params?.userName || "test-user",
    role: params?.role || "MENTOR",
  });

  // api 인스턴스 사용 (baseURL에 이미 /api/v1 포함)
  const response = await api.get(`/test/livekit/token?${queryParams.toString()}`);

  return response.data;
};

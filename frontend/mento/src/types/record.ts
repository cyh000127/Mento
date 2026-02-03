/**
 * Egress 녹화 요청을 위한 페이로드 인터페이스
 */
export interface EgressRequestPayload {
  roomId: string; // LiveKit Room Name
  mentorId: string;
  audioTrackSid?: string; // 멘토 마이크
  videoTrackSid?: string; // 멘토 캠 화면
}

/**
 * Egress 녹화 응답 데이터 타입
 */
export interface EgressResponse {
  egressId: string;
  status: string;
}

/**
 * 녹화 API 공통 응답 래퍼
 * { success: boolean, data: T, error: ... }
 */
export interface RecordApiResponse<T> {
  success: boolean;
  data: T;
  error: { message: string } | null;
}

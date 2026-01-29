/**
 * LiveKit 테스트 토큰 발급 API 응답 타입
 * GET /api/v1/test/livekit/token
 */
export interface LivekitTestTokenResponse {
  roomToken: string;
  roomName: string;
  livekitUrl: string;
  participantRole: "MENTOR" | "USER";
  enteredAt: string; // ISO String
}

/**
 * LiveKit 테스트 토큰 발급 API 요청 파라미터
 */
export interface LivekitTestTokenParams {
  roomName?: string;
  userName?: string;
  role?: "MENTOR" | "USER";
}

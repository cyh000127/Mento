import { useEffect, useRef } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { useLivekitTestSession } from "@/hooks/useLivekitTestSession";
import { VideoTrack } from "@/components/consultation/VideoTrack";
import { SidePanel } from "@/components/consultation/side-panel";
import { useConsultationStore } from "@/stores/useConsultationStore";

/**
 * LiveKit 테스트 페이지 (테스트 전용)
 *
 * 사용 방법:
 * - MENTOR 역할: /livekit-test?roomName=test-room&userName=mentor-1&role=MENTOR
 * - USER 역할: /livekit-test?roomName=test-room&userName=user-1&role=USER
 *
 * ⚠️ 주의사항:
 * - 로그인 없이 접속 가능합니다.
 * - 동일한 roomName으로 최대 2명까지만 접속할 수 있습니다.
 * - role은 MENTOR와 USER 중 하나여야 합니다.
 */
export function LivekitTestPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { connectionState, error, sessionData, localParticipant, remoteParticipants, connect, toggleMic, toggleCamera, disconnect, isMicEnabled, isCameraEnabled } = useLivekitTestSession();
  const { selectedMaskArea } = useConsultationStore();

  const hasConnected = useRef(false);

  // 쿼리 파라미터에서 값 추출
  const roomName = searchParams.get("roomName") || "test-room";
  const userName = searchParams.get("userName") || `user-${Math.random().toString(36).substring(7)}`;
  const role = (searchParams.get("role") || "MENTOR") as "MENTOR" | "USER";

  // 컴포넌트 마운트 시 자동으로 LiveKit 연결 (한 번만)
  useEffect(() => {
    if (hasConnected.current) return;

    hasConnected.current = true;
    connect({
      roomName,
      userName,
      role,
    });
  }, [roomName, userName, role, connect]);

  // 연결 해제 핸들러
  const handleDisconnect = () => {
    disconnect();
    navigate("/");
  };

  // role에 따라 비디오 배치 결정
  const isMentor = sessionData?.participantRole === "MENTOR";

  // MENTOR: 본인이 컨설턴트(위), 상대방이 고객(아래)
  // USER: 상대방이 컨설턴트(위), 본인이 고객(아래)
  const topParticipant = isMentor ? localParticipant : remoteParticipants[0];
  const bottomParticipant = isMentor ? remoteParticipants[0] : localParticipant;
  const topLabel = isMentor ? `컨설턴트 (나)` : remoteParticipants[0] ? `컨설턴트 (${remoteParticipants[0].identity})` : "컨설턴트";
  const bottomLabel = isMentor ? (remoteParticipants[0] ? `고객 (${remoteParticipants[0].identity})` : "고객") : `고객 (나)`;

  // 디버깅: 참가자 상태 콘솔 출력
  useEffect(() => {
    console.log("📊 참가자 상태 업데이트:", {
      role: sessionData?.participantRole,
      hasLocalParticipant: !!localParticipant,
      localParticipantIdentity: localParticipant?.identity,
      remoteParticipantsCount: remoteParticipants.length,
      topParticipant: topParticipant ? { identity: topParticipant.identity, isLocal: topParticipant === localParticipant } : null,
      bottomParticipant: bottomParticipant ? { identity: bottomParticipant.identity, isLocal: bottomParticipant === localParticipant } : null,
    });
  }, [localParticipant, remoteParticipants, sessionData, topParticipant, bottomParticipant]);

  return (
    <div className="relative h-screen bg-gray-950 overflow-hidden">
      {/* 테스트 모드 표시 배너 */}
      <div className="absolute top-0 left-0 right-0 bg-yellow-600 text-white text-center py-2 px-4 z-0">
        <span className="font-bold">🧪 테스트 모드</span>
        <span className="ml-4 text-sm">
          Room: {roomName} | Role: {role} | User: {userName}
        </span>
      </div>

      {/* 메인 컨텐츠 영역 */}
      <div className="h-full pt-12 pr-96">
        <div className="h-full flex flex-col items-center justify-center p-8">
          {/* 연결 상태 표시 */}
          {connectionState === "connecting" && <div className="absolute top-16 left-4 bg-yellow-600 text-white px-4 py-2 rounded-lg shadow-lg">연결 중...</div>}
          {connectionState === "connected" && <div className="absolute top-16 left-4 bg-green-600 text-white px-4 py-2 rounded-lg shadow-lg">✓ 연결됨</div>}
          {error && <div className="absolute top-16 left-4 bg-red-600 text-white px-4 py-2 rounded-lg shadow-lg max-w-md">오류: {error}</div>}

          {/* 참가자 수 표시 */}
          <div className="absolute top-16 right-4 bg-gray-800 text-white px-4 py-2 rounded-lg shadow-lg border border-gray-700">
            <span className="text-sm">
              참가자: {localParticipant ? 1 : 0} + {remoteParticipants.length} / 2
            </span>
          </div>

          {/* 컨설턴트 비디오 영역 (상단) */}
          <div className="w-full max-w-2xl mb-8">
            <div className="aspect-video bg-gray-800 rounded-lg shadow-2xl border border-gray-700 overflow-hidden relative">
              {topParticipant ? (
                <>
                  <VideoTrack participant={topParticipant} maskType={null} />
                  <div className="absolute bottom-4 left-4 bg-black/60 text-white px-3 py-1 rounded text-sm">{topLabel}</div>
                </>
              ) : (
                <div className="w-full h-full flex items-center justify-center">
                  <div className="text-center">
                    <div className="text-6xl mb-4">👨‍⚕️</div>
                    <p className="text-xl text-gray-400 font-semibold">컨설턴트</p>
                    <p className="text-sm text-gray-500 mt-2">대기 중...</p>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* 고객 비디오 영역 (하단) - 사이드 패널에서 선택한 마스크 적용 */}
          <div className="w-full max-w-2xl">
            <div className="aspect-video bg-gray-800 rounded-lg shadow-xl border border-gray-700 overflow-hidden relative">
              {bottomParticipant ? (
                <>
                  <VideoTrack participant={bottomParticipant} maskType={selectedMaskArea} />
                  <div className="absolute bottom-4 left-4 bg-black/60 text-white px-3 py-1 rounded text-sm">
                    {bottomLabel}
                    {selectedMaskArea && (
                      <span className="ml-2 text-xs text-cyan-300">🎭 {selectedMaskArea} 분석 중</span>
                    )}
                  </div>
                </>
              ) : (
                <div className="w-full h-full flex items-center justify-center">
                  <div className="text-center">
                    <div className="text-4xl mb-3">👤</div>
                    <p className="text-lg text-gray-400 font-semibold">고객</p>
                    <p className="text-xs text-gray-500 mt-2">대기 중...</p>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* 하단 컨트롤 바 */}
          <div className="fixed bottom-8 left-1/2 -translate-x-[calc(50%+12rem)]">
            <div className="bg-gray-800 rounded-full px-6 py-4 shadow-2xl border border-gray-700 flex items-center gap-4">
              {/* 마이크 버튼 */}
              <button
                className={`p-3 rounded-full transition-colors ${isMicEnabled ? "bg-gray-700 hover:bg-gray-600" : "bg-red-600 hover:bg-red-700"}`}
                onClick={toggleMic}
                title={isMicEnabled ? "마이크 끄기" : "마이크 켜기"}
              >
                {isMicEnabled ? (
                  <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z" />
                  </svg>
                ) : (
                  <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z"
                      clipPath="url(#mic-clip)"
                    />
                    <defs>
                      <clipPath id="mic-clip">
                        <path d="M0 0h24v24H0z" />
                      </clipPath>
                    </defs>
                    <line x1="4" y1="4" x2="20" y2="20" strokeLinecap="round" strokeWidth={2} />
                  </svg>
                )}
              </button>

              {/* 카메라 버튼 */}
              <button
                className={`p-3 rounded-full transition-colors ${isCameraEnabled ? "bg-gray-700 hover:bg-gray-600" : "bg-red-600 hover:bg-red-700"}`}
                onClick={toggleCamera}
                title={isCameraEnabled ? "카메라 끄기" : "카메라 켜기"}
              >
                {isCameraEnabled ? (
                  <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"
                    />
                  </svg>
                ) : (
                  <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"
                    />
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3l18 18" />
                  </svg>
                )}
              </button>

              {/* 통화 종료 버튼 */}
              <button className="p-3 rounded-full bg-red-600 hover:bg-red-700 transition-colors" onClick={handleDisconnect} title="통화 종료">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
      {/* 오른쪽 사이드 패널 */}
      <SidePanel />
    </div>
  );
}

import { useEffect, useRef, useMemo } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useConsultationSession } from "@/hooks/useConsultationSession";
import { VideoTrack } from "@/components/consultation/VideoTrack";
import { SidePanel } from "@/components/consultation/side-panel";
import { useConsultationStore } from "@/stores/useConsultationStore";
import { useAuthStore } from "@/stores/useAuthStore";

export function ConsultationRoomPage() {
  const { roomId } = useParams<{ roomId: string }>();
  const reservationId = useMemo(() => {
    if (!roomId) return null;
    try {
      return atob(roomId);
    } catch (e) {
      console.error("Invalid room ID format", e);
      return null;
    }
  }, [roomId]);

  const navigate = useNavigate();
  const { user } = useAuthStore();
  const {
    connectionState,
    error,
    sessionData,
    localParticipant,
    remoteParticipants,
    remoteMaskType,
    remoteMaskUpdateSeq,
    sharedImageUrl,
    drawCommands,
    connect,
    disconnect,
    sendMaskUpdate,
    sendMediaShare,
    sendImageShare,
    sendImageClear,
    sendDrawCommand,
    sendClearWhiteboard,
    toggleMic,
    toggleCamera,
    isMicEnabled,
    isCameraEnabled,
  } = useConsultationSession();
  const { selectedMaskArea, setActiveTab, setSelectedMaskArea } = useConsultationStore();

  const hasConnected = useRef(false);
  const isApplyingRemoteMaskRef = useRef(false);
  const hasResetMaskOnConnectRef = useRef(false);
  const reservationIdNumber = reservationId ? Number(reservationId) : null;

  // 컴포넌트 마운트 시 자동으로 상담 세션 생성 및 LiveKit 연결
  useEffect(() => {
    if (!reservationId) {
      console.error("❌ reservationId가 없습니다.");
      navigate("/");
      return;
    }

    if (hasConnected.current) return;

    hasConnected.current = true;
    const id = parseInt(reservationId, 10);

    if (isNaN(id)) {
      console.error("❌ 유효하지 않은 reservationId:", reservationId);
      navigate("/");
      return;
    }

    // 디버깅: 토큰 상태 확인
    console.log("🔑 인증 상태 확인:", {
      hasRefreshToken: localStorage.getItem("hasRefreshToken"),
      reservationId: id,
      roomId: roomId // Log obfuscated ID
    });

    connect(id);
  }, [reservationId, roomId, connect, navigate]);

  // 연결 해제 핸들러
  const handleDisconnect = () => {
    sendImageClear();
    sendClearWhiteboard();
    disconnect();
    navigate("/mypage/consultations");
  };

  // participantRole 기준으로 항상 MENTOR는 상단, USER는 하단
  const isMentor = sessionData?.participantRole === "MENTOR";
  const mentorParticipant = isMentor ? localParticipant : remoteParticipants[0];
  const userParticipant = isMentor ? remoteParticipants[0] : localParticipant;

  const topParticipant = mentorParticipant;
  const bottomParticipant = userParticipant;
  const sharedMaskType = selectedMaskArea;

  const topLabel = isMentor ? "멘토 (나)" : mentorParticipant ? `멘토 (${mentorParticipant.identity})` : "멘토";
  const bottomLabel = isMentor ? (userParticipant ? `고객 (${userParticipant.identity})` : "고객") : "고객 (나)";

  const sidePanelTabs = isMentor ? (["share", "inventory", "mask", "record"] as const) : (["share", "inventory"] as const);
  const recordProps = {
    isMentor,
    roomId: sessionData?.roomName ?? null,
    mentorId: user?.id ?? null,
    localParticipant,
    connectionState,
  };
  const shareProps = {
    reservationId: Number.isNaN(reservationIdNumber ?? NaN) ? null : reservationIdNumber,
    onShare: sendMediaShare,
    sharedImageUrl,
    drawCommands,
    onShareImage: sendImageShare,
    onDrawCommand: sendDrawCommand,
    onClearWhiteboard: sendClearWhiteboard,
    canDraw: isMentor,
  };

  useEffect(() => {
    const handleUnload = () => {
      sessionStorage.setItem("consultation:clearOnReconnect", "1");
      sendImageClear();
      sendClearWhiteboard();
    };

    window.addEventListener("beforeunload", handleUnload);
    window.addEventListener("pagehide", handleUnload);

    return () => {
      window.removeEventListener("beforeunload", handleUnload);
      window.removeEventListener("pagehide", handleUnload);
    };
  }, [sendImageClear, sendClearWhiteboard]);

  useEffect(() => {
    if (connectionState !== "connected") return;
    const shouldClear = sessionStorage.getItem("consultation:clearOnReconnect") === "1";
    if (!shouldClear) return;
    sessionStorage.removeItem("consultation:clearOnReconnect");
    sendImageClear();
    sendClearWhiteboard();
  }, [connectionState, sendImageClear, sendClearWhiteboard]);

  // USER 접근 시 숨겨진 탭이 활성화되지 않도록 초기화
  useEffect(() => {
    if (sessionData?.participantRole === "USER") {
      setActiveTab("share");
    }
  }, [sessionData?.participantRole, setActiveTab]);

  // 원격에서 받은 마스크를 로컬 상태에 반영 (단일 소스 유지)
  useEffect(() => {
    if (remoteMaskUpdateSeq === 0) return;
    isApplyingRemoteMaskRef.current = true;
    setSelectedMaskArea(remoteMaskType);
  }, [remoteMaskType, remoteMaskUpdateSeq, setSelectedMaskArea]);

  // 마스크 변경을 DataChannel로 전송 (역방향 에코 방지)
  useEffect(() => {
    if (connectionState !== "connected") return;
    if (isApplyingRemoteMaskRef.current) {
      isApplyingRemoteMaskRef.current = false;
      return;
    }
    sendMaskUpdate(selectedMaskArea);
  }, [connectionState, selectedMaskArea, sendMaskUpdate]);

  // 새로고침/재접속 시 마스크 초기화
  useEffect(() => {
    if (connectionState !== "connected") {
      hasResetMaskOnConnectRef.current = false;
      return;
    }
    if (hasResetMaskOnConnectRef.current) return;
    hasResetMaskOnConnectRef.current = true;
    setSelectedMaskArea(null);
  }, [connectionState, setSelectedMaskArea]);

  return (
    <div className="relative h-screen bg-gray-950 overflow-hidden">
      {/* 메인 컨텐츠 영역 */}
      <div className="h-full pr-[26rem]">
        <div className="h-full flex items-center gap-6 px-8 pt-8 pb-28">
          {/* 왼쪽 (멘토) */}
          <div className="flex-1 h-full max-w-[50%]">
            <div className="h-full flex flex-col">
              <div className="flex-1 aspect-video bg-gray-800 rounded-lg shadow-2xl border border-gray-700 overflow-hidden relative">
                {topParticipant ? (
                  <>
                    <VideoTrack participant={topParticipant} maskType={sharedMaskType} />
                    <div className="absolute bottom-4 left-4 bg-black/60 text-white px-3 py-1 rounded text-sm">
                      {topLabel}
                      {sharedMaskType && <span className="ml-2 text-xs text-cyan-300">🎭 {sharedMaskType} 분석 중</span>}
                    </div>
                  </>
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-gray-500">멘토 대기 중...</div>
                )}
              </div>
            </div>
          </div>

          {/* 오른쪽 (고객) */}
          <div className="flex-1 h-full max-w-[50%]">
            <div className="h-full flex flex-col">
              <div className="flex-1 aspect-video bg-gray-800 rounded-lg shadow-xl border border-gray-700 overflow-hidden relative">
                {bottomParticipant ? (
                  <>
                    <VideoTrack participant={bottomParticipant} maskType={sharedMaskType} />
                    <div className="absolute bottom-4 left-4 bg-black/60 text-white px-3 py-1 rounded text-sm">
                      {bottomLabel}
                      {sharedMaskType && <span className="ml-2 text-xs text-cyan-300">🎭 {sharedMaskType} 분석 중</span>}
                    </div>
                  </>
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-gray-500">고객 대기 중...</div>
                )}
              </div>
            </div>
          </div>

          {/* 연결 상태 표시 */}
          {connectionState === "connecting" && <div className="absolute top-4 left-4 bg-yellow-600 text-white px-4 py-2 rounded-lg shadow-lg">연결 중...</div>}
          {connectionState === "connected" && <div className="absolute top-4 left-4 bg-green-600 text-white px-4 py-2 rounded-lg shadow-lg">✓ 연결됨</div>}
          {error && <div className="absolute top-4 left-4 bg-red-600 text-white px-4 py-2 rounded-lg shadow-lg max-w-md">오류: {error}</div>}

          {/* 하단 컨트롤 바 */}
          <div className="fixed bottom-4 left-[calc((100%-26rem)/2)] -translate-x-1/2">
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

              {/* AI 리포트 생성 버튼 (멘토 전용) */}
              {isMentor && (
                <button className="h-12 px-4 py-2 rounded-full bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium transition-colors" type="button" title="AI 리포트 생성">
                  AI 리포트 요청
                </button>
              )}

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

      {/* 사이드 패널 */}
      <SidePanel allowedTabs={sidePanelTabs} recordProps={recordProps} shareProps={shareProps} />
    </div>
  );
}

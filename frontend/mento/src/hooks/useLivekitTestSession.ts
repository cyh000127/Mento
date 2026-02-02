import { useState, useEffect, useCallback, useRef } from "react";
import { Room, RoomEvent, RemoteParticipant, LocalParticipant } from "livekit-client";
import { fetchLivekitTestToken } from "@/api/livekittestApi";
import type { LivekitTestTokenResponse, LivekitTestTokenParams } from "@/types/livekitTest";

export type ConnectionState = "disconnected" | "connecting" | "connected" | "error";

export interface UseLivekitTestSessionReturn {
  // 연결 상태
  connectionState: ConnectionState;
  error: string | null;

  // 세션 정보
  sessionData: LivekitTestTokenResponse | null;

  // 참가자 정보
  localParticipant: LocalParticipant | null;
  remoteParticipants: RemoteParticipant[];

  // 연결 제어
  connect: (params?: LivekitTestTokenParams) => Promise<void>;
  disconnect: () => void;

  // 미디어 제어
  toggleMic: () => Promise<void>;
  toggleCamera: () => Promise<void>;
  isMicEnabled: boolean;
  isCameraEnabled: boolean;
}

/**
 * LiveKit 테스트 연결을 관리하는 커스텀 훅 (테스트 전용)
 *
 * 주요 기능:
 * - 테스트 토큰 API 호출 (GET /api/v1/test/livekit/token)
 * - LiveKit Room 연결 및 해제
 * - 로컬/원격 참가자 상태 관리 (최대 2명 제한)
 * - 자동 카메라/마이크 활성화
 * - 연결 상태 추적
 *
 * ⚠️ 주의사항:
 * - 이 훅은 테스트 전용이며, 실제 예약 기반 상담과 분리되어 있습니다.
 * - 인증이 필요 없으며, 로그인 상태와 무관합니다.
 * - 한 방에는 최대 2명(로컬 1명 + 원격 1명)만 접속할 수 있습니다.
 */
export function useLivekitTestSession(): UseLivekitTestSessionReturn {
  const [room, setRoom] = useState<Room | null>(null);
  const [connectionState, setConnectionState] = useState<ConnectionState>("disconnected");
  const [error, setError] = useState<string | null>(null);
  const [sessionData, setSessionData] = useState<LivekitTestTokenResponse | null>(null);
  const [localParticipant, setLocalParticipant] = useState<LocalParticipant | null>(null);
  const [remoteParticipants, setRemoteParticipants] = useState<RemoteParticipant[]>([]);
  const [isMicEnabled, setIsMicEnabled] = useState(true);
  const [isCameraEnabled, setIsCameraEnabled] = useState(true);

  // 중복 연결 방지를 위한 ref
  const isConnecting = useRef(false);

  /**
   * LiveKit 테스트 세션 연결
   */
  const connect = useCallback(
    async (params?: LivekitTestTokenParams) => {
      // 중복 연결 방지
      if (isConnecting.current || connectionState !== "disconnected") {
        console.warn("⚠️ 이미 연결 중이거나 연결되어 있습니다.");
        return;
      }

      try {
        isConnecting.current = true;
        setConnectionState("connecting");
        setError(null);

        // 1. 테스트 토큰 API 호출
        console.log("📞 테스트 토큰 발급 요청:", params);
        const session = await fetchLivekitTestToken(params);
        console.log("✅ 테스트 토큰 발급 완료:", session);

        setSessionData(session);

        if (!session.roomToken || !session.livekitUrl) {
          throw new Error("테스트 토큰 또는 URL을 받지 못했습니다.");
        }

        // 2. Room 인스턴스 생성
        const newRoom = new Room({
          adaptiveStream: true,
          dynacast: true,
        });

        // 3. 이벤트 리스너 등록

        // 연결 성공
        newRoom.on(RoomEvent.Connected, () => {
          console.log("✅ LiveKit Room 연결 성공");
          setConnectionState("connected");
          setLocalParticipant(newRoom.localParticipant);

          // 기존 참가자 확인 (이미 방에 있던 사람들)
          /*
          const existingParticipants = Array.from(newRoom.remoteParticipants.values());
          console.log(`📊 현재 방에 ${existingParticipants.length}명의 원격 참가자가 있습니다.`);

          // 참가자 수 제한 체크 (로컬 1명 + 원격 최대 1명 = 총 2명)
          if (existingParticipants.length > 1) {
            console.error("❌ 방이 가득 찼습니다. (최대 2명 제한)");
            setError("방이 가득 찼습니다. 나중에 다시 시도해주세요.");
            newRoom.disconnect();
            return;
          }

          setRemoteParticipants(existingParticipants);
          */

          // 임시 수정: 전체 참가자가 아닌 '사람'만 필터링, livekit-agent는 제외
          const remoteParticipants = Array.from(newRoom.remoteParticipants.values());
          const remoteHumans = remoteParticipants.filter(p => !p.identity.includes('agent'));

          console.log(`📊 현재 방 인원: 전체 ${remoteParticipants.length}명 (사람: ${remoteHumans.length}명)`);

          // 사람만 기준으로 1명 초과(즉, 내가 들어가면 3명째가 되는 상황)면 막기
          if (remoteHumans.length > 1) {
              console.error("❌ 방이 가득 찼습니다. (사람 2명 제한)");
              setError("방이 가득 찼습니다. 나중에 다시 시도해주세요.");
              newRoom.disconnect();
              return;
          }
        });


        // 로컬 트랙 publish 이벤트 (카메라/마이크 활성화 시)
        newRoom.on(RoomEvent.LocalTrackPublished, (publication, participant) => {
          console.log(`🎤 로컬 트랙 published: kind=${publication.kind}`);
          // 로컬 참가자 상태 업데이트 (트랙이 추가되었으므로)
          setLocalParticipant(participant);
        });

        // 연결 해제
        newRoom.on(RoomEvent.Disconnected, () => {
          console.log("❌ LiveKit Room 연결 해제");
          setConnectionState("disconnected");
          setLocalParticipant(null);
          setRemoteParticipants([]);
          isConnecting.current = false;
        });

        // 원격 참가자 입장
        newRoom.on(RoomEvent.ParticipantConnected, (participant) => {
          console.log("✅ 원격 참가자 연결:", participant.identity);

          setRemoteParticipants((prev) => {
            // 참가자 수 제한 체크 (로컬 1명 + 원격 최대 1명 = 총 2명)
            if (prev.length >= 1) {
              console.error("❌ 방이 가득 찼습니다. 새로운 참가자를 차단합니다.");
              setError("방이 가득 찼습니다.");
              // 새로운 참가자는 추가하지 않음
              return prev;
            }

            return [...prev, participant];
          });
        });

        // 트랙 구독 이벤트 (원격 참가자의 트랙이 사용 가능해질 때)
        newRoom.on(RoomEvent.TrackSubscribed, (track, _publication, participant) => {
          console.log(`📹 트랙 구독 완료: ${participant.identity}, kind: ${track.kind}`);
          // 상태 업데이트를 위해 remoteParticipants 배열 재설정
          setRemoteParticipants((prev) => [...prev]);
        });

        // 원격 참가자 퇴장
        newRoom.on(RoomEvent.ParticipantDisconnected, (participant) => {
          console.log("❌ 원격 참가자 연결 해제:", participant.identity);
          setRemoteParticipants((prev) => prev.filter((rp) => rp.sid !== participant.sid));
        });

        // 4. LiveKit Room 연결
        console.log(`🔌 LiveKit 서버 연결 시도: ${session.livekitUrl}`);
        await newRoom.connect(session.livekitUrl, session.roomToken);

        // 5. Room 인스턴스 먼저 저장 (이벤트 리스너가 작동하도록)
        setRoom(newRoom);

        // 6. 카메라와 마이크 활성화
        console.log("🎥 카메라와 마이크 활성화 중...");
        await newRoom.localParticipant.enableCameraAndMicrophone();

        // 7. 트랙이 완전히 publish될 때까지 대기
        console.log("⏳ 트랙 publish 대기 중...");
        await new Promise<void>((resolve) => {
          const checkTracks = () => {
            const videoTrack = Array.from(newRoom.localParticipant.trackPublications.values()).find((pub) => pub.kind === "video");
            const audioTrack = Array.from(newRoom.localParticipant.trackPublications.values()).find((pub) => pub.kind === "audio");

            if (videoTrack && audioTrack) {
              console.log("✅ 모든 트랙 publish 완료");
              resolve();
            } else {
              console.log("⏳ 트랙 대기 중... (video:", !!videoTrack, "audio:", !!audioTrack, ")");
              setTimeout(checkTracks, 100);
            }
          };
          checkTracks();
        });

        // 8. 미디어 상태 업데이트
        setIsMicEnabled(true);
        setIsCameraEnabled(true);

        isConnecting.current = false;
        console.log("✅ LiveKit 테스트 세션 연결 완료");
      } catch (e) {
        console.error("❌ 테스트 세션 연결 실패:", e);
        setConnectionState("error");
        setError(e instanceof Error ? e.message : "테스트 세션 연결에 실패했습니다.");
        isConnecting.current = false;
      }
    },
    [connectionState],
  );

  /**
   * LiveKit Room 연결 해제
   */
  const disconnect = useCallback(() => {
    console.log("🔌 연결 해제 요청");
    room?.disconnect();
    setRoom(null);
    setLocalParticipant(null);
    setRemoteParticipants([]);
    setConnectionState("disconnected");
    setSessionData(null);
    setError(null);
    isConnecting.current = false;
  }, [room]);

  /**
   * 마이크 토글
   */
  const toggleMic = useCallback(async () => {
    if (!room) return;

    const enabled = room.localParticipant.isMicrophoneEnabled;
    await room.localParticipant.setMicrophoneEnabled(!enabled);
    setIsMicEnabled(!enabled);
  }, [room]);

  /**
   * 카메라 토글
   */
  const toggleCamera = useCallback(async () => {
    if (!room) return;

    const enabled = room.localParticipant.isCameraEnabled;
    await room.localParticipant.setCameraEnabled(!enabled);
    setIsCameraEnabled(!enabled);
  }, [room]);

  /**
   * 컴포넌트 언마운트 시 연결 해제
   */
  useEffect(() => {
    return () => {
      if (room) {
        console.log("🧹 컴포넌트 언마운트: 연결 해제");
        room.disconnect();
      }
    };
  }, [room]);

  return {
    connectionState,
    error,
    sessionData,
    localParticipant,
    remoteParticipants,
    connect,
    disconnect,
    toggleMic,
    toggleCamera,
    isMicEnabled,
    isCameraEnabled,
  };
}

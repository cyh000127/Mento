import { useState, useCallback, useEffect, useRef } from "react";
import { Room, RoomEvent, RemoteParticipant, LocalParticipant } from "livekit-client";
import { createConsultationSession } from "@/api/consultationSessionApi";
import type { ConsultationSessionData } from "@/types/consultation";
import type { MaskType } from "@/hooks/useFaceMask";
import type { MediaFileType, SharedMediaFile, DrawCommand } from "@/types/consultationMedia";

export type ConnectionState = "disconnected" | "connecting" | "connected" | "error";

export interface UseConsultationSessionReturn {
  // 연결 상태
  connectionState: ConnectionState;
  error: string | null;

  // 세션 정보
  sessionData: ConsultationSessionData | null;

  // 참가자 정보
  localParticipant: LocalParticipant | null;
  remoteParticipants: RemoteParticipant[];
  remoteMaskType: MaskType;
  remoteMaskUpdateSeq: number;
  sharedMediaFiles: SharedMediaFile[];
  sharedImageUrl: string | null;
  drawCommands: DrawCommand[];

  // 연결 제어
  connect: (reservationId: number) => Promise<void>;
  disconnect: () => void;
  sendMaskUpdate: (maskType: MaskType) => void;
  sendMediaShare: (files: SharedMediaFile[]) => void;
  sendImageShare: (imageUrl: string) => void;
  sendImageClear: () => void;
  sendDrawCommand: (command: DrawCommand) => void;
  sendClearWhiteboard: () => void;

  // 미디어 제어
  toggleMic: () => Promise<void>;
  toggleCamera: () => Promise<void>;
  isMicEnabled: boolean;
  isCameraEnabled: boolean;
}

/**
 * 예약 기반 실제 상담 세션을 관리하는 커스텀 훅
 *
 * ⚠️ 주의사항:
 * - 이 훅은 실제 예약 기반 상담 전용이며, LiveKit 테스트와 완전히 분리되어 있습니다.
 * - 인증이 필요하며, 세션 생성 API에서 받은 토큰만 사용합니다.
 * - 환경변수나 테스트 토큰을 절대 사용하지 않습니다.
 */
export function useConsultationSession(): UseConsultationSessionReturn {
  const [room, setRoom] = useState<Room | null>(null);
  const [connectionState, setConnectionState] = useState<ConnectionState>("disconnected");
  const [error, setError] = useState<string | null>(null);
  const [sessionData, setSessionData] = useState<ConsultationSessionData | null>(null);
  const [localParticipant, setLocalParticipant] = useState<LocalParticipant | null>(null);
  const [remoteParticipants, setRemoteParticipants] = useState<RemoteParticipant[]>([]);
  const [remoteMaskType, setRemoteMaskType] = useState<MaskType>(null);
  const [remoteMaskUpdateSeq, setRemoteMaskUpdateSeq] = useState(0);
  const [sharedMediaFiles, setSharedMediaFiles] = useState<SharedMediaFile[]>([]);
  const [sharedImageUrl, setSharedImageUrl] = useState<string | null>(null);
  const [drawCommands, setDrawCommands] = useState<DrawCommand[]>([]);
  const [isMicEnabled, setIsMicEnabled] = useState(true);
  const [isCameraEnabled, setIsCameraEnabled] = useState(true);

  // 중복 연결 및 API 호출 방지를 위한 ref
  const isConnecting = useRef(false);
  const processedReservationId = useRef<number | null>(null);

  const isValidMaskType = (value: unknown): value is MaskType => {
    return value === null || value === "T-zone" || value === "U-zone" || value === "Nose zone" || value === "Apple zone";
  };

  const parseMaskUpdate = (payload: Uint8Array): MaskType | undefined => {
    try {
      const text = new TextDecoder().decode(payload);
      const data = JSON.parse(text) as { type?: string; value?: unknown };
      if (data?.type !== "MASK_UPDATE") return undefined;
      if (!("value" in data)) return undefined;
      return isValidMaskType(data.value) ? data.value : undefined;
    } catch (error) {
      console.warn("⚠️ 마스크 데이터 파싱 실패:", error);
      return undefined;
    }
  };

  const isValidMediaFileType = (value: unknown): value is MediaFileType => {
    return value === "IMAGE" || value === "VIDEO";
  };

  const parseMediaShare = (payload: Uint8Array): SharedMediaFile[] | undefined => {
    try {
      const text = new TextDecoder().decode(payload);
      const data = JSON.parse(text) as { type?: string; files?: unknown };
      if (data?.type !== "MEDIA_SHARED") return undefined;
      if (!Array.isArray(data.files)) return undefined;
      const files = data.files
        .map((file) => {
          const record = file as { fileUrl?: unknown; fileType?: unknown };
          if (typeof record?.fileUrl !== "string") return null;
          if (!isValidMediaFileType(record.fileType)) return null;
          return { fileUrl: record.fileUrl, fileType: record.fileType };
        })
        .filter((file): file is SharedMediaFile => Boolean(file));
      return files.length > 0 ? files : undefined;
    } catch (error) {
      console.warn("⚠️ 미디어 데이터 파싱 실패:", error);
      return undefined;
    }
  };

  const parseImageShare = (payload: Uint8Array): string | undefined => {
    try {
      const text = new TextDecoder().decode(payload);
      const data = JSON.parse(text) as { type?: string; imageUrl?: unknown };
      if (data?.type !== "IMAGE_SHARED") return undefined;
      if (typeof data.imageUrl !== "string") return undefined;
      return data.imageUrl;
    } catch (error) {
      console.warn("⚠️ 이미지 공유 데이터 파싱 실패:", error);
      return undefined;
    }
  };

  const parseImageClear = (payload: Uint8Array): boolean => {
    try {
      const text = new TextDecoder().decode(payload);
      const data = JSON.parse(text) as { type?: string };
      return data?.type === "IMAGE_CLEAR";
    } catch (error) {
      console.warn("⚠️ 이미지 초기화 데이터 파싱 실패:", error);
      return false;
    }
  };

  const parseDrawCommand = (payload: Uint8Array): DrawCommand | undefined => {
    try {
      const text = new TextDecoder().decode(payload);
      const data = JSON.parse(text) as { type?: string; tool?: unknown; color?: unknown; lineWidth?: unknown; points?: unknown };
      if (data?.type !== "DRAW") return undefined;
      if (data.tool !== "pen") return undefined;
      if (typeof data.color !== "string") return undefined;
      if (typeof data.lineWidth !== "number") return undefined;
      if (!Array.isArray(data.points)) return undefined;
      const points = data.points
        .map((point) => {
          const record = point as { x?: unknown; y?: unknown };
          if (typeof record.x !== "number" || typeof record.y !== "number") return null;
          return { x: record.x, y: record.y };
        })
        .filter((point): point is { x: number; y: number } => Boolean(point));
      if (points.length < 2) return undefined;
      return { tool: "pen", color: data.color, lineWidth: data.lineWidth, points };
    } catch (error) {
      console.warn("⚠️ 드로잉 데이터 파싱 실패:", error);
      return undefined;
    }
  };

  const parseWhiteboardClear = (payload: Uint8Array): boolean => {
    try {
      const text = new TextDecoder().decode(payload);
      const data = JSON.parse(text) as { type?: string };
      return data?.type === "WHITEBOARD_CLEAR";
    } catch (error) {
      console.warn("⚠️ 화이트보드 초기화 데이터 파싱 실패:", error);
      return false;
    }
  };

  /**
   * 상담 세션 생성 및 LiveKit 연결
   */
  const connect = useCallback(
    async (reservationId: number) => {
      // 중복 연결 방지
      if (isConnecting.current || connectionState !== "disconnected") {
        console.warn("⚠️ 이미 연결 중이거나 연결되어 있습니다.");
        return;
      }

      // 같은 예약 ID로 중복 API 호출 방지
      if (processedReservationId.current === reservationId) {
        console.warn("⚠️ 이미 처리된 예약입니다.");
        return;
      }

      try {
        isConnecting.current = true;
        setConnectionState("connecting");
        setError(null);

        // 1. 상담 세션 생성 API 호출 (LiveKit 자격증명 발급)
        console.log("📞 상담 세션 생성 요청:", { reservationId });
        const session = await createConsultationSession(reservationId);
        console.log("✅ 상담 세션 생성 완료:", session);

        setSessionData(session);
        processedReservationId.current = reservationId;
        session.livekitUrl = "wss://i14a704.p.ssafy.io/rtc/";
        
        if (!session.roomToken || !session.livekitUrl || !session.roomName) {
          throw new Error("LiveKit 접속 정보가 올바르지 않습니다.");
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

          // 기존 원격 참가자 확인 (에이전트 제외)
          const allRemoteParticipants = Array.from(newRoom.remoteParticipants.values());
          const remoteHumans = allRemoteParticipants.filter((p) => !p.identity.includes("agent"));

          console.log(`📊 현재 방 인원: 전체 ${allRemoteParticipants.length}명 (사람: ${remoteHumans.length}명)`);

          setRemoteParticipants(remoteHumans);
        });

        // 로컬 트랙 publish 이벤트
        newRoom.on(RoomEvent.LocalTrackPublished, (publication, participant) => {
          console.log(`🎤 로컬 트랙 published: kind=${publication.kind}`);
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

        // 원격 참가자 입장 (에이전트 제외)
        newRoom.on(RoomEvent.ParticipantConnected, (participant) => {
          console.log("✅ 원격 참가자 연결:", participant.identity);

          if (participant.identity.includes("agent")) {
            console.log("🤖 에이전트 입장 감지 (UI 표시 생략)");
            return;
          }

          setRemoteParticipants((prev) => [...prev, participant]);
        });

        // 트랙 구독 이벤트
        newRoom.on(RoomEvent.TrackSubscribed, (track, _publication, participant) => {
          console.log(`📹 트랙 구독 완료: ${participant.identity}, kind: ${track.kind}`);
          setRemoteParticipants((prev) => [...prev]);
        });

        // 데이터 채널 수신 (마스크 동기화)
        newRoom.on(RoomEvent.DataReceived, (payload, _participant) => {
          const nextMaskType = parseMaskUpdate(payload);
          if (nextMaskType !== undefined) {
            setRemoteMaskType(nextMaskType);
            setRemoteMaskUpdateSeq((prev) => prev + 1);
            return;
          }

          const sharedFiles = parseMediaShare(payload);
          if (sharedFiles) {
            setSharedMediaFiles((prev) => {
              const existing = new Set(prev.map((file) => file.fileUrl));
              const merged = [...prev];
              sharedFiles.forEach((file) => {
                if (!existing.has(file.fileUrl)) {
                  existing.add(file.fileUrl);
                  merged.push(file);
                }
              });
              return merged;
            });
            return;
          }

          const imageUrl = parseImageShare(payload);
          if (imageUrl) {
            setSharedImageUrl(imageUrl);
            setDrawCommands([]);
            return;
          }

        if (parseImageClear(payload)) {
          setSharedImageUrl(null);
          setDrawCommands([]);
          return;
        }

          if (parseWhiteboardClear(payload)) {
            setDrawCommands([]);
            return;
          }

          const drawCommand = parseDrawCommand(payload);
          if (!drawCommand) return;
          setDrawCommands((prev) => [...prev, drawCommand]);
        });

        // 원격 참가자 퇴장
        newRoom.on(RoomEvent.ParticipantDisconnected, (participant) => {
          console.log("❌ 원격 참가자 연결 해제:", participant.identity);
          setRemoteParticipants((prev) => prev.filter((rp) => rp.sid !== participant.sid));
        });

        // 4. LiveKit Room 연결
        console.log(`🔌 LiveKit 서버 연결 시도: ${session.livekitUrl}`);
        await newRoom.connect(session.livekitUrl, session.roomToken);

        // 5. Room 인스턴스 저장
        setRoom(newRoom);

        // 6. 카메라와 마이크 활성화
        console.log("🎥 카메라와 마이크 활성화 중...");
        await newRoom.localParticipant.enableCameraAndMicrophone();

        // 7. 트랙 publish 대기
        console.log("⏳ 트랙 publish 대기 중...");
        await new Promise<void>((resolve) => {
          const checkTracks = () => {
            const videoTrack = Array.from(newRoom.localParticipant.trackPublications.values()).find((pub) => pub.kind === "video");
            const audioTrack = Array.from(newRoom.localParticipant.trackPublications.values()).find((pub) => pub.kind === "audio");

            if (videoTrack && audioTrack) {
              console.log("✅ 모든 트랙 publish 완료");
              resolve();
            } else {
              setTimeout(checkTracks, 100);
            }
          };
          checkTracks();
        });

        // 8. 미디어 상태 업데이트
        setIsMicEnabled(true);
        setIsCameraEnabled(true);
        setRemoteMaskType(null);
        setRemoteMaskUpdateSeq(0);
        setSharedMediaFiles([]);
        setSharedImageUrl(null);
        setDrawCommands([]);

        isConnecting.current = false;
        console.log("✅ 상담 세션 연결 완료");
      } catch (e) {
        console.error("❌ 상담 세션 연결 실패:", e);
        setConnectionState("error");
        const errorMessage = e instanceof Error ? e.message : "상담 세션 연결에 실패했습니다.";
        setError(errorMessage);
        isConnecting.current = false;
        processedReservationId.current = null;
      }
    },
    [connectionState]
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
    setRemoteMaskType(null);
    setConnectionState("disconnected");
    setSessionData(null);
    setSharedMediaFiles([]);
    setSharedImageUrl(null);
    setDrawCommands([]);
    setError(null);
    isConnecting.current = false;
    processedReservationId.current = null;
  }, [room]);

  /**
   * 마스크 상태 전송 (DataChannel)
   */
  const sendMaskUpdate = useCallback(
    (maskType: MaskType) => {
      if (!room) return;
      const payload = { type: "MASK_UPDATE", value: maskType };
      const data = new TextEncoder().encode(JSON.stringify(payload));
      room.localParticipant.publishData(data, { reliable: true });
    },
    [room]
  );

  /**
   * 이미지 공유 데이터 전송 (DataChannel)
   */
  const sendImageShare = useCallback(
    (imageUrl: string) => {
      setSharedImageUrl(imageUrl);
      setDrawCommands([]);
      if (!room) return;
      const payload = { type: "IMAGE_SHARED", imageUrl };
      const data = new TextEncoder().encode(JSON.stringify(payload));
      room.localParticipant.publishData(data, { reliable: true });
    },
    [room]
  );

  const sendImageClear = useCallback(() => {
    setSharedImageUrl(null);
    setDrawCommands([]);
    if (!room) return;
    const payload = { type: "IMAGE_CLEAR" };
    const data = new TextEncoder().encode(JSON.stringify(payload));
    room.localParticipant.publishData(data, { reliable: true });
  }, [room]);

  /**
   * 드로잉 데이터 전송 (DataChannel)
   */
  const sendDrawCommand = useCallback(
    (command: DrawCommand) => {
      if (!room) return;
      const payload = { type: "DRAW", ...command };
      const data = new TextEncoder().encode(JSON.stringify(payload));
      room.localParticipant.publishData(data, { reliable: true });
    },
    [room]
  );

  /**
   * 화이트보드 초기화 전송 (DataChannel)
   */
  const sendClearWhiteboard = useCallback(() => {
    setDrawCommands([]);
    if (!room) return;
    const payload = { type: "WHITEBOARD_CLEAR" };
    const data = new TextEncoder().encode(JSON.stringify(payload));
    room.localParticipant.publishData(data, { reliable: true });
  }, [room]);

  /**
   * 미디어 공유 데이터 전송 (DataChannel)
   */
  const sendMediaShare = useCallback(
    (files: SharedMediaFile[]) => {
      if (!room || files.length === 0) return;
      const payload = { type: "MEDIA_SHARED", files };
      const data = new TextEncoder().encode(JSON.stringify(payload));
      room.localParticipant.publishData(data, { reliable: true });
    },
    [room]
  );

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
    remoteMaskType,
    remoteMaskUpdateSeq,
    sharedMediaFiles,
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
  };
}

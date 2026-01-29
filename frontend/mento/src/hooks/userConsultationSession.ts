// import { useState, useEffect, useCallback } from "react";
// import { Room, RoomEvent, RemoteParticipant, LocalParticipant } from "livekit-client";
// import { createConsultationSession } from "@/api/sessionApi";
// import { useConsultationStore } from "@/stores/useConsultationStore";
// import type { ConsultationSession } from "@/types/session";

// export type ConnectionState = "disconnected" | "connecting" | "connected" | "error";

// export interface UseConsultationSessionReturn {
//   // 연결 상태
//   connectionState: ConnectionState;
//   error: string | null;

//   // 세션 정보
//   sessionData: ConsultationSession | null;

//   // 참가자 정보
//   localParticipant: LocalParticipant | null;
//   remoteParticipants: RemoteParticipant[];

//   // Room 인스턴스 (비디오 렌더링용)
//   room: Room | null;

//   // 연결 제어
//   connect: (reservationId: number) => Promise<void>;
//   disconnect: () => void;

//   // 미디어 제어
//   toggleMic: () => Promise<void>;
//   toggleCamera: () => Promise<void>;
//   isMicEnabled: boolean;
//   isCameraEnabled: boolean;
// }

// /**
//  * 실제 상담 세션 연결을 관리하는 커스텀 훅
//  *
//  * 주요 기능:
//  * - 예약 ID 기반 세션 생성 (POST /api/v1/reservations/{id}/sessions)
//  * - LiveKit Room 연결 및 해제
//  * - 로컬/원격 참가자 상태 관리
//  * - 자동 카메라/마이크 활성화
//  * - 연결 상태 추적
//  */
// export function useConsultationSession(): UseConsultationSessionReturn {
//   const [room, setRoom] = useState<Room | null>(null);
//   const [connectionState, setConnectionState] = useState<ConnectionState>("disconnected");
//   const [error, setError] = useState<string | null>(null);
//   const [sessionData, setSessionData] = useState<ConsultationSession | null>(null);
//   const [localParticipant, setLocalParticipant] = useState<LocalParticipant | null>(null);
//   const [remoteParticipants, setRemoteParticipants] = useState<RemoteParticipant[]>([]);
//   const [isMicEnabled, setIsMicEnabled] = useState(true);
//   const [isCameraEnabled, setIsCameraEnabled] = useState(true);

//   // Zustand store에서 세션 관리 액션 가져오기
//   const { setCurrentSession, clearSession } = useConsultationStore();

//   const connect = useCallback(
//     async (reservationId: number) => {
//       // 이미 연결 중이거나 연결되어 있으면 중복 연결 방지
//       if (connectionState !== "disconnected") return;

//       try {
//         setConnectionState("connecting");
//         setError(null);

//         // 1. 백엔드에서 세션 생성 및 토큰 받기
//         console.log("📞 세션 생성 요청:", reservationId);
//         const session = await createConsultationSession(reservationId);
//         console.log("✅ 세션 생성 완료:", session);

//         setSessionData(session);
//         // Zustand store에 세션 데이터 저장
//         setCurrentSession(session);

//         if (!session.roomToken || !session.livekitUrl) {
//           throw new Error("세션 토큰 또는 URL을 받지 못했습니다.");
//         }

//         // 2. Room 인스턴스 생성
//         const newRoom = new Room({
//           adaptiveStream: true,
//           dynacast: true,
//         });

//         // 3. 이벤트 리스너 등록

//         // 연결 성공
//         newRoom.on(RoomEvent.Connected, () => {
//           console.log("✅ LiveKit Room 연결 성공");
//           setConnectionState("connected");
//           setLocalParticipant(newRoom.localParticipant);

//           // 기존 참가자 확인
//           const existingParticipants = Array.from(newRoom.remoteParticipants.values());
//           console.log(`📊 현재 방에 ${existingParticipants.length}명의 원격 참가자가 있습니다.`);
//           setRemoteParticipants(existingParticipants);
//         });

//         // 연결 해제
//         newRoom.on(RoomEvent.Disconnected, () => {
//           console.log("❌ LiveKit Room 연결 해제");
//           setConnectionState("disconnected");
//           setLocalParticipant(null);
//           setRemoteParticipants([]);
//         });

//         // 원격 참가자 입장
//         newRoom.on(RoomEvent.ParticipantConnected, (participant) => {
//           console.log("✅ 원격 참가자 연결:", participant.identity);
//           setRemoteParticipants((prev) => [...prev, participant]);
//         });

//         // 원격 참가자 퇴장
//         newRoom.on(RoomEvent.ParticipantDisconnected, (participant) => {
//           console.log("❌ 원격 참가자 연결 해제:", participant.identity);
//           setRemoteParticipants((prev) => prev.filter((rp) => rp.sid !== participant.sid));
//         });

//         // 로컬 트랙 publish 이벤트
//         newRoom.on(RoomEvent.LocalTrackPublished, (publication, participant) => {
//           console.log(`🎤 로컬 트랙 published: kind=${publication.kind}`);
//           setLocalParticipant(participant);
//         });

//         // 원격 트랙 구독 이벤트
//         newRoom.on(RoomEvent.TrackSubscribed, (track, _publication, participant) => {
//           console.log(`📹 트랙 구독 완료: ${participant.identity}, kind: ${track.kind}`);
//           setRemoteParticipants((prev) => [...prev]);
//         });

//         // 4. LiveKit Room 연결
//         console.log(`🔌 LiveKit 서버 연결 시도: ${session.livekitUrl}`);
//         await newRoom.connect(session.livekitUrl, session.roomToken);

//         // 5. Room 인스턴스 먼저 저장
//         setRoom(newRoom);

//         // 6. 카메라와 마이크 활성화
//         console.log("🎥 카메라와 마이크 활성화 중...");
//         await newRoom.localParticipant.enableCameraAndMicrophone();

//         // 7. 트랙이 완전히 publish될 때까지 대기
//         console.log("⏳ 트랙 publish 대기 중...");
//         await new Promise<void>((resolve) => {
//           const checkTracks = () => {
//             const videoTrack = Array.from(newRoom.localParticipant.trackPublications.values()).find((pub) => pub.kind === "video");
//             const audioTrack = Array.from(newRoom.localParticipant.trackPublications.values()).find((pub) => pub.kind === "audio");

//             if (videoTrack && audioTrack) {
//               console.log("✅ 모든 트랙 publish 완료");
//               resolve();
//             } else {
//               setTimeout(checkTracks, 100);
//             }
//           };
//           checkTracks();
//         });

//         // 8. 미디어 상태 업데이트
//         setIsMicEnabled(true);
//         setIsCameraEnabled(true);

//         console.log("✅ 상담 세션 연결 완료");
//       } catch (e) {
//         console.error("❌ 세션 연결 실패:", e);
//         setConnectionState("error");
//         setError(e instanceof Error ? e.message : "세션 연결에 실패했습니다.");
//       }
//     },
//     [connectionState],
//   );

//   // LiveKit Room 연결 해제
//   const disconnect = useCallback(() => {
//     room?.disconnect();
//     setRoom(null);
//     setLocalParticipant(null);
//     setRemoteParticipants([]);
//     setConnectionState("disconnected");
//     setSessionData(null);
//     setError(null);
//     // Zustand store에서 세션 데이터 제거
//     clearSession();
//   }, [room, clearSession]);

//   // 마이크 토글
//   const toggleMic = useCallback(async () => {
//     if (!room) return;

//     const enabled = room.localParticipant.isMicrophoneEnabled;
//     await room.localParticipant.setMicrophoneEnabled(!enabled);
//     setIsMicEnabled(!enabled);
//   }, [room]);

//   // 카메라 토글
//   const toggleCamera = useCallback(async () => {
//     if (!room) return;

//     const enabled = room.localParticipant.isCameraEnabled;
//     await room.localParticipant.setCameraEnabled(!enabled);
//     setIsCameraEnabled(!enabled);
//   }, [room]);

//   // 컴포넌트 언마운트 시 연결 해제
//   useEffect(() => {
//     return () => {
//       room?.disconnect();
//     };
//   }, [room]);

//   return {
//     connectionState,
//     error,
//     sessionData,
//     localParticipant,
//     remoteParticipants,
//     room,
//     connect,
//     disconnect,
//     toggleMic,
//     toggleCamera,
//     isMicEnabled,
//     isCameraEnabled,
//   };
// }

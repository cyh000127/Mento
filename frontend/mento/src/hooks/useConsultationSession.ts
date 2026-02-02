import { useState, useCallback, useEffect, useRef } from "react"
import { Room, RoomEvent, RemoteParticipant, LocalParticipant } from "livekit-client"
import { createConsultationSession } from "@/api/consultationSessionApi"
import type { ConsultationSessionData } from "@/types/consultation"

export type ConnectionState = "disconnected" | "connecting" | "connected" | "error"

export interface UseConsultationSessionReturn {
  // 연결 상태
  connectionState: ConnectionState
  error: string | null

  // 세션 정보
  sessionData: ConsultationSessionData | null

  // 참가자 정보
  localParticipant: LocalParticipant | null
  remoteParticipants: RemoteParticipant[]

  // 연결 제어
  connect: (reservationId: number) => Promise<void>
  disconnect: () => void

  // 미디어 제어
  toggleMic: () => Promise<void>
  toggleCamera: () => Promise<void>
  isMicEnabled: boolean
  isCameraEnabled: boolean
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
  const [room, setRoom] = useState<Room | null>(null)
  const [connectionState, setConnectionState] = useState<ConnectionState>("disconnected")
  const [error, setError] = useState<string | null>(null)
  const [sessionData, setSessionData] = useState<ConsultationSessionData | null>(null)
  const [localParticipant, setLocalParticipant] = useState<LocalParticipant | null>(null)
  const [remoteParticipants, setRemoteParticipants] = useState<RemoteParticipant[]>([])
  const [isMicEnabled, setIsMicEnabled] = useState(true)
  const [isCameraEnabled, setIsCameraEnabled] = useState(true)

  // 중복 연결 및 API 호출 방지를 위한 ref
  const isConnecting = useRef(false)
  const processedReservationId = useRef<number | null>(null)

  /**
   * 상담 세션 생성 및 LiveKit 연결
   */
  const connect = useCallback(
    async (reservationId: number) => {
      // 중복 연결 방지
      if (isConnecting.current || connectionState !== "disconnected") {
        console.warn("⚠️ 이미 연결 중이거나 연결되어 있습니다.")
        return
      }

      // 같은 예약 ID로 중복 API 호출 방지
      if (processedReservationId.current === reservationId) {
        console.warn("⚠️ 이미 처리된 예약입니다.")
        return
      }

      try {
        isConnecting.current = true
        setConnectionState("connecting")
        setError(null)

        // 1. 상담 세션 생성 API 호출 (LiveKit 자격증명 발급)
        console.log("📞 상담 세션 생성 요청:", { reservationId })
        const session = await createConsultationSession(reservationId)
        console.log("✅ 상담 세션 생성 완료:", session)

        setSessionData(session)
        processedReservationId.current = reservationId

        if (!session.roomToken || !session.livekitUrl || !session.roomName) {
          throw new Error("LiveKit 접속 정보가 올바르지 않습니다.")
        }

        // 2. Room 인스턴스 생성
        const newRoom = new Room({
          adaptiveStream: true,
          dynacast: true,
        })

        // 3. 이벤트 리스너 등록

        // 연결 성공
        newRoom.on(RoomEvent.Connected, () => {
          console.log("✅ LiveKit Room 연결 성공")
          setConnectionState("connected")
          setLocalParticipant(newRoom.localParticipant)

          // 기존 원격 참가자 확인 (에이전트 제외)
          const allRemoteParticipants = Array.from(newRoom.remoteParticipants.values())
          const remoteHumans = allRemoteParticipants.filter(
            (p) => !p.identity.includes("agent")
          )

          console.log(
            `📊 현재 방 인원: 전체 ${allRemoteParticipants.length}명 (사람: ${remoteHumans.length}명)`
          )

          setRemoteParticipants(remoteHumans)
        })

        // 로컬 트랙 publish 이벤트
        newRoom.on(RoomEvent.LocalTrackPublished, (publication, participant) => {
          console.log(`🎤 로컬 트랙 published: kind=${publication.kind}`)
          setLocalParticipant(participant)
        })

        // 연결 해제
        newRoom.on(RoomEvent.Disconnected, () => {
          console.log("❌ LiveKit Room 연결 해제")
          setConnectionState("disconnected")
          setLocalParticipant(null)
          setRemoteParticipants([])
          isConnecting.current = false
        })

        // 원격 참가자 입장 (에이전트 제외)
        newRoom.on(RoomEvent.ParticipantConnected, (participant) => {
          console.log("✅ 원격 참가자 연결:", participant.identity)

          if (participant.identity.includes("agent")) {
            console.log("🤖 에이전트 입장 감지 (UI 표시 생략)")
            return
          }

          setRemoteParticipants((prev) => [...prev, participant])
        })

        // 트랙 구독 이벤트
        newRoom.on(RoomEvent.TrackSubscribed, (track, _publication, participant) => {
          console.log(`📹 트랙 구독 완료: ${participant.identity}, kind: ${track.kind}`)
          setRemoteParticipants((prev) => [...prev])
        })

        // 원격 참가자 퇴장
        newRoom.on(RoomEvent.ParticipantDisconnected, (participant) => {
          console.log("❌ 원격 참가자 연결 해제:", participant.identity)
          setRemoteParticipants((prev) => prev.filter((rp) => rp.sid !== participant.sid))
        })

        // 4. LiveKit Room 연결
        console.log(`🔌 LiveKit 서버 연결 시도: ${session.livekitUrl}`)
        await newRoom.connect(session.livekitUrl, session.roomToken)

        // 5. Room 인스턴스 저장
        setRoom(newRoom)

        // 6. 카메라와 마이크 활성화
        console.log("🎥 카메라와 마이크 활성화 중...")
        await newRoom.localParticipant.enableCameraAndMicrophone()

        // 7. 트랙 publish 대기
        console.log("⏳ 트랙 publish 대기 중...")
        await new Promise<void>((resolve) => {
          const checkTracks = () => {
            const videoTrack = Array.from(
              newRoom.localParticipant.trackPublications.values()
            ).find((pub) => pub.kind === "video")
            const audioTrack = Array.from(
              newRoom.localParticipant.trackPublications.values()
            ).find((pub) => pub.kind === "audio")

            if (videoTrack && audioTrack) {
              console.log("✅ 모든 트랙 publish 완료")
              resolve()
            } else {
              setTimeout(checkTracks, 100)
            }
          }
          checkTracks()
        })

        // 8. 미디어 상태 업데이트
        setIsMicEnabled(true)
        setIsCameraEnabled(true)

        isConnecting.current = false
        console.log("✅ 상담 세션 연결 완료")
      } catch (e) {
        console.error("❌ 상담 세션 연결 실패:", e)
        setConnectionState("error")
        const errorMessage = e instanceof Error ? e.message : "상담 세션 연결에 실패했습니다."
        setError(errorMessage)
        isConnecting.current = false
        processedReservationId.current = null
      }
    },
    [connectionState]
  )

  /**
   * LiveKit Room 연결 해제
   */
  const disconnect = useCallback(() => {
    console.log("🔌 연결 해제 요청")
    room?.disconnect()
    setRoom(null)
    setLocalParticipant(null)
    setRemoteParticipants([])
    setConnectionState("disconnected")
    setSessionData(null)
    setError(null)
    isConnecting.current = false
    processedReservationId.current = null
  }, [room])

  /**
   * 마이크 토글
   */
  const toggleMic = useCallback(async () => {
    if (!room) return

    const enabled = room.localParticipant.isMicrophoneEnabled
    await room.localParticipant.setMicrophoneEnabled(!enabled)
    setIsMicEnabled(!enabled)
  }, [room])

  /**
   * 카메라 토글
   */
  const toggleCamera = useCallback(async () => {
    if (!room) return

    const enabled = room.localParticipant.isCameraEnabled
    await room.localParticipant.setCameraEnabled(!enabled)
    setIsCameraEnabled(!enabled)
  }, [room])

  /**
   * 컴포넌트 언마운트 시 연결 해제
   */
  useEffect(() => {
    return () => {
      if (room) {
        console.log("🧹 컴포넌트 언마운트: 연결 해제")
        room.disconnect()
      }
    }
  }, [room])

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
  }
}

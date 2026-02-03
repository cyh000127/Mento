import { useEffect, useRef, useState } from "react";
import { Track, LocalParticipant, RemoteParticipant, TrackPublication, ParticipantEvent } from "livekit-client";
import { useFaceMask, type MaskType } from "@/hooks/useFaceMask";

interface VideoTrackProps {
  participant: LocalParticipant | RemoteParticipant;
  maskType?: MaskType; // 마스크 타입 (null이면 마스크 비활성화)
}

/**
 * LiveKit 참가자의 비디오 트랙을 렌더링하는 컴포넌트
 *
 * 로컬 참가자와 원격 참가자를 구분하여 처리:
 * - 로컬: trackPublications에서 직접 트랙을 가져와 attach
 * - 원격: TrackSubscribed 이벤트를 통해 트랙을 받아 attach
 */
export function VideoTrack({ participant, maskType = null }: VideoTrackProps) {
  const videoRef = useRef<HTMLVideoElement>(null);
  const audioRef = useRef<HTMLAudioElement>(null);
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const isLocal = participant instanceof LocalParticipant;
  // 트랙 변경을 감지하기 위한 state (강제 리렌더링용)
  const [trackUpdateCount, setTrackUpdateCount] = useState(0);
  const retryTimeoutRef = useRef<number | null>(null);
  const [videoElementReady, setVideoElementReady] = useState(false);

  // 얼굴 마스크 훅 초기화 (비디오 요소가 준비된 후에만 실행)
  useFaceMask(
    videoElementReady ? videoRef.current : null,
    canvasRef,
    maskType
  );

  // 비디오 요소가 준비되었을 때 감지
  useEffect(() => {
    const videoElement = videoRef.current;
    if (!videoElement) return;

    const handleLoadedData = () => {
      console.log("📹 비디오 요소 준비 완료:", {
        identity: participant.identity,
        videoWidth: videoElement.videoWidth,
        videoHeight: videoElement.videoHeight,
      });
      setVideoElementReady(true);
    };

    if (videoElement.readyState >= 2) {
      handleLoadedData();
    } else {
      videoElement.addEventListener("loadeddata", handleLoadedData);
    }

    return () => {
      videoElement.removeEventListener("loadeddata", handleLoadedData);
    };
  }, [participant.identity]);

  // 트랙 업데이트 시 재연결을 위한 별도 useEffect
  useEffect(() => {
    if (trackUpdateCount === 0) return; // 초기 렌더링은 스킵

    const videoElement = videoRef.current;
    if (!videoElement) return;

    console.log(`🔄 트랙 업데이트 감지 (count: ${trackUpdateCount}) - 재연결 시도`);

    const publications = participant.trackPublications as Map<string, TrackPublication>;
    const videoPublication = Array.from(publications.values()).find((pub) => pub.kind === Track.Kind.Video);

    if (videoPublication?.track && videoElement) {
      console.log(`🎥 트랙 재연결: ${participant.identity}`);
      videoPublication.track.attach(videoElement);
    }
  }, [trackUpdateCount, participant, isLocal]);

  useEffect(() => {
    const videoElement = videoRef.current;
    const audioElement = audioRef.current;
    if (!videoElement || !audioElement) return;

    console.log(`🔍 VideoTrack 마운트: ${participant.identity}, 타입: ${isLocal ? "Local" : "Remote"}`);

    let currentVideoTrack: Track | null = null;
    let currentAudioTrack: Track | null = null;

    /**
     * 비디오 트랙 찾기 및 연결
     */
    const getVideoPublication = () => {
      if (typeof participant.getTrackPublication === "function") {
        const bySource = participant.getTrackPublication(Track.Source.Camera);
        if (bySource) return bySource;
      }

      const publications = participant.trackPublications as Map<string, TrackPublication>;
      return Array.from(publications.values()).find((pub) => pub.kind === Track.Kind.Video);
    };

    /**
     * 오디오 트랙 찾기
     */
    const getAudioPublication = () => {
      if (typeof participant.getTrackPublication === "function") {
        const bySource = participant.getTrackPublication(Track.Source.Microphone);
        if (bySource) return bySource;
      }

      const publications = participant.trackPublications as Map<string, TrackPublication>;
      return Array.from(publications.values()).find((pub) => pub.kind === Track.Kind.Audio);
    };

    const attachVideoTrack = () => {
      // 기존 트랙이 있으면 먼저 해제
      if (currentVideoTrack) {
        console.log(`🔄 기존 비디오 트랙 해제: ${participant.identity}`);
        currentVideoTrack.detach(videoElement);
        currentVideoTrack = null;
      }

      if (currentAudioTrack) {
        console.log(`🔄 기존 오디오 트랙 해제: ${participant.identity}`);
        currentAudioTrack.detach(audioElement);
        currentAudioTrack = null;
      }

      // 트랙 publications에서 비디오/오디오 트랙 찾기
      const publications = participant.trackPublications as Map<string, TrackPublication>;
      console.log(`🔍 트랙 검색 중: ${participant.identity} (${isLocal ? "Local" : "Remote"})`, {
        publicationsCount: publications.size,
        allPublications: Array.from(publications.values()).map((p) => ({
          kind: p.kind,
          trackSid: p.trackSid,
          hasTrack: !!p.track,
          trackName: p.trackName,
        })),
      });

      const videoPublication = getVideoPublication();
      const audioPublication = getAudioPublication();

      // 비디오 트랙 연결
      if (videoPublication?.track) {
        console.log(`🎥 비디오 트랙 연결 시작: ${participant.identity} (${isLocal ? "Local" : "Remote"})`);
        console.log(`   트랙 정보:`, {
          trackSid: videoPublication.trackSid,
          trackName: videoPublication.trackName,
          isMuted: videoPublication.isMuted,
        });

        videoPublication.track.attach(videoElement);
        currentVideoTrack = videoPublication.track;

        console.log(`✅ 비디오 트랙 연결 완료: ${participant.identity}`);
        console.log(`   video element 상태:`, {
          videoWidth: videoElement.videoWidth,
          videoHeight: videoElement.videoHeight,
          srcObject: !!videoElement.srcObject,
        });
      } else {
        console.warn(`❌ 비디오 트랙 없음: ${participant.identity}`, {
          hasPublication: !!videoPublication,
          hasTrack: videoPublication?.track ? true : false,
          publicationsCount: publications.size,
        });

        if (isLocal) {
          if (retryTimeoutRef.current) {
            window.clearTimeout(retryTimeoutRef.current);
          }
          retryTimeoutRef.current = window.setTimeout(() => {
            attachVideoTrack();
          }, 200);
        }
      }

      // 오디오 트랙 연결 (로컬 참가자는 제외 - 에코 방지)
      if (!isLocal && audioPublication?.track) {
        console.log(`🎤 오디오 트랙 연결 시작: ${participant.identity}`);
        console.log(`   트랙 정보:`, {
          trackSid: audioPublication.trackSid,
          trackName: audioPublication.trackName,
          isMuted: audioPublication.isMuted,
        });

        audioPublication.track.attach(audioElement);
        currentAudioTrack = audioPublication.track;

        console.log(`✅ 오디오 트랙 연결 완료: ${participant.identity}`);
      } else if (isLocal) {
        console.log(`🔇 로컬 참가자 오디오는 연결하지 않음 (에코 방지)`);
      } else {
        console.warn(`❌ 오디오 트랙 없음: ${participant.identity}`, {
          hasPublication: !!audioPublication,
          hasTrack: audioPublication?.track ? true : false,
        });
      }
    };

    // 초기 트랙 연결 시도
    attachVideoTrack();

    /**
     * 로컬 참가자 전용 처리
     */
    if (isLocal) {
      // 로컬 트랙 publish 이벤트 리스너
      const handleLocalTrackPublished = (publication: TrackPublication) => {
        console.log(`📡 로컬 트랙 published: kind=${publication.kind}, trackSid=${publication.trackSid}`);
        if (publication.kind === Track.Kind.Video) {
          console.log(`🔄 비디오 트랙 publish 감지 - 리렌더링 트리거`);
          // 트랙이 완전히 준비될 때까지 약간 대기 후 강제 리렌더링
          setTimeout(() => {
            attachVideoTrack();
            setTrackUpdateCount((prev) => prev + 1);
          }, 100);
        }
      };

      participant.on(ParticipantEvent.TrackPublished, handleLocalTrackPublished);

      // 클린업
      return () => {
        console.log(`🧹 VideoTrack 언마운트 (Local): ${participant.identity}`);
        if (currentVideoTrack) {
          currentVideoTrack.detach(videoElement);
        }
        if (currentAudioTrack) {
          currentAudioTrack.detach(audioElement);
        }
        if (retryTimeoutRef.current) {
          window.clearTimeout(retryTimeoutRef.current);
          retryTimeoutRef.current = null;
        }
        participant.off(ParticipantEvent.TrackPublished, handleLocalTrackPublished);
      };
    } else {
      /**
       * 원격 참가자 전용 처리
       */
      // 트랙 subscribed 이벤트 리스너
      const handleTrackSubscribed = (track: Track, publication: TrackPublication) => {
        console.log(`✅ 원격 트랙 subscribed: ${participant.identity}, kind=${publication.kind}`);
        if (publication.kind === Track.Kind.Video) {
          // 기존 트랙 해제
          if (currentVideoTrack) {
            currentVideoTrack.detach(videoElement);
          }
          // 새 트랙 연결
          track.attach(videoElement);
          currentVideoTrack = track;
        } else if (publication.kind === Track.Kind.Audio) {
          // 기존 오디오 트랙 해제
          if (currentAudioTrack) {
            currentAudioTrack.detach(audioElement);
          }
          // 새 오디오 트랙 연결
          track.attach(audioElement);
          currentAudioTrack = track;
          console.log(`✅ 원격 오디오 트랙 연결 완료: ${participant.identity}`);
        }
      };

      // 트랙 unsubscribed 이벤트 리스너
      const handleTrackUnsubscribed = (_track: Track, publication: TrackPublication) => {
        console.log(`❌ 원격 트랙 unsubscribed: ${participant.identity}, kind=${publication.kind}`);
        if (publication.kind === Track.Kind.Video && currentVideoTrack) {
          currentVideoTrack.detach(videoElement);
          currentVideoTrack = null;
        } else if (publication.kind === Track.Kind.Audio && currentAudioTrack) {
          currentAudioTrack.detach(audioElement);
          currentAudioTrack = null;
        }
      };

      participant.on(ParticipantEvent.TrackSubscribed, handleTrackSubscribed);
      participant.on(ParticipantEvent.TrackUnsubscribed, handleTrackUnsubscribed);

      // 클린업
      return () => {
        console.log(`🧹 VideoTrack 언마운트 (Remote): ${participant.identity}`);
        if (currentVideoTrack) {
          currentVideoTrack.detach(videoElement);
        }
        if (currentAudioTrack) {
          currentAudioTrack.detach(audioElement);
        }
        if (retryTimeoutRef.current) {
          window.clearTimeout(retryTimeoutRef.current);
          retryTimeoutRef.current = null;
        }
        participant.off(ParticipantEvent.TrackSubscribed, handleTrackSubscribed);
        participant.off(ParticipantEvent.TrackUnsubscribed, handleTrackUnsubscribed);
      };
    }
  }, [participant, isLocal]);

  return (
    <div ref={containerRef} className="relative w-full h-full">
      {/* LiveKit 비디오 */}
      <video ref={videoRef} className="w-full h-full object-cover" autoPlay playsInline muted={isLocal} />
      
      {/* 얼굴 마스크 오버레이 캔버스 */}
      <canvas
        ref={canvasRef}
        className="absolute top-0 left-0 w-full h-full pointer-events-none"
        style={{
          objectFit: "cover",
          opacity: maskType ? 1 : 0,
        }}
      />
      
      {/* 오디오 엘리먼트 (원격 참가자의 소리를 재생) */}
      <audio ref={audioRef} autoPlay playsInline />
    </div>
  );
}

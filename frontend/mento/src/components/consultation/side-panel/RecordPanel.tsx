import { useCallback, useEffect, useMemo, useState } from "react";
import { LocalParticipant, Track } from "livekit-client";
import { startRecording, stopRecording } from "@/api/record";
import type { EgressRequestPayload } from "@/types/record";
import type { ConnectionState } from "@/hooks/useConsultationSession";

export interface RecordPanelProps {
  isMentor?: boolean;
  roomId?: string | null;
  mentorId?: number | null;
  localParticipant?: LocalParticipant | null;
  connectionState?: ConnectionState;
}

export function RecordPanel({
  isMentor = false,
  roomId,
  mentorId,
  localParticipant,
  connectionState = "disconnected",
}: RecordPanelProps) {
  const [isRecording, setIsRecording] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [egressId, setEgressId] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const isConnected = connectionState === "connected";
  const canRecord = useMemo(() => {
    return Boolean(isMentor && isConnected && roomId && mentorId && localParticipant);
  }, [isMentor, isConnected, roomId, mentorId, localParticipant]);

  const getTrackSid = useCallback(
    (source: Track.Source) => {
      if (!localParticipant) return undefined;
      const publication = localParticipant.getTrackPublication?.(source);
      if (publication?.trackSid) return publication.trackSid;
      return undefined;
    },
    [localParticipant]
  );

  const handleStart = useCallback(async () => {
    if (!canRecord || !roomId || !mentorId || !localParticipant) return;
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const payload: EgressRequestPayload = {
        roomId,
        mentorId: mentorId.toString(),
        audioTrackSid: getTrackSid(Track.Source.Microphone),
        videoTrackSid: getTrackSid(Track.Source.Camera),
      };
      const response = await startRecording(payload);
      setEgressId(response.egressId);
      setIsRecording(true);
    } catch (error) {
      console.error("녹화 시작 실패:", error);
      setErrorMessage(error instanceof Error ? error.message : "녹화 시작에 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  }, [canRecord, roomId, mentorId, localParticipant, getTrackSid]);

  const handleStop = useCallback(async () => {
    if (!roomId) return;
    setIsLoading(true);
    setErrorMessage(null);
    try {
      await stopRecording(roomId);
      setIsRecording(false);
      setEgressId(null);
    } catch (error) {
      console.error("녹화 종료 실패:", error);
      setErrorMessage(error instanceof Error ? error.message : "녹화 종료에 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  }, [roomId]);

  useEffect(() => {
    if (!isConnected) {
      setIsRecording(false);
      setEgressId(null);
    }
  }, [isConnected]);

  return (
    <div className="flex flex-col h-full p-6 text-gray-200">
      <div className="mb-6">
        <h3 className="text-lg font-semibold text-gray-100">녹화</h3>
        <p className="text-sm text-gray-400 mt-1">녹화는 멘토만 시작/종료할 수 있습니다.</p>
      </div>

      {!isMentor ? (
        <div className="flex-1 flex items-center justify-center text-center text-gray-400">
          <div>
            <div className="text-4xl mb-3">🔒</div>
            <p className="text-sm">멘토 전용 기능입니다.</p>
          </div>
        </div>
      ) : (
        <div className="flex-1 flex flex-col gap-4">
          <div className="rounded-lg border border-gray-700 bg-gray-800/60 p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-400">상태</p>
                <p className={`text-base font-semibold ${isRecording ? "text-red-400" : "text-gray-100"}`}>
                  {isRecording ? "녹화 중" : "대기"}
                </p>
              </div>
              <div className={`h-3 w-3 rounded-full ${isRecording ? "bg-red-500 animate-pulse" : "bg-gray-500"}`} />
            </div>
            {egressId && (
              <p className="mt-2 text-xs text-gray-400">
                Egress ID: {egressId}
              </p>
            )}
          </div>

          <div className="grid grid-cols-2 gap-3">
            <button
              className="py-3 rounded-lg bg-red-600 hover:bg-red-700 disabled:bg-gray-700 disabled:text-gray-400 transition-colors"
              onClick={handleStart}
              disabled={!canRecord || isRecording || isLoading}
            >
              {isLoading && !isRecording ? "시작 중..." : "녹화 시작"}
            </button>
            <button
              className="py-3 rounded-lg bg-gray-700 hover:bg-gray-600 disabled:bg-gray-800 disabled:text-gray-500 transition-colors"
              onClick={handleStop}
              disabled={!roomId || !isRecording || isLoading}
            >
              {isLoading && isRecording ? "종료 중..." : "녹화 종료"}
            </button>
          </div>

          {!isConnected && (
            <p className="text-xs text-yellow-400">LiveKit 연결 후 녹화를 시작할 수 있습니다.</p>
          )}
          {errorMessage && (
            <p className="text-xs text-red-400">{errorMessage}</p>
          )}
        </div>
      )}
    </div>
  );
}

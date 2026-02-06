import { useEffect, useRef, useState, useMemo } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Mic, MicOff, Video, VideoOff } from "lucide-react";

export function ConsultationWaitingRoomPage() {
    const { roomId } = useParams<{ roomId: string }>();
    const navigate = useNavigate();
    const videoRef = useRef<HTMLVideoElement>(null);

    const reservationId = useMemo(() => {
        if (!roomId) return null;
        try {
            return atob(roomId);
        } catch (e) {
            console.error("Invalid room ID format", e);
            return null;
        }
    }, [roomId]);

    const [hasPermission, setHasPermission] = useState(false);
    const [stream, setStream] = useState<MediaStream | null>(null);
    const [isMicEnabled, setIsMicEnabled] = useState(true);
    const [isCameraEnabled, setIsCameraEnabled] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let localStream: MediaStream | null = null;

        const initMedia = async () => {
            try {
                localStream = await navigator.mediaDevices.getUserMedia({
                    video: true,
                    audio: true,
                });

                setStream(localStream);
                setHasPermission(true);

                if (videoRef.current) {
                    videoRef.current.srcObject = localStream;
                }
            } catch (err) {
                console.error("Failed to access media devices:", err);
                setError("카메라 또는 마이크에 접근할 수 없습니다. 권한을 확인해주세요.");
                setHasPermission(false);
            }
        };

        initMedia();

        return () => {
            if (localStream) {
                localStream.getTracks().forEach(track => track.stop());
            }
        };
    }, []);

    // 토글 상태 변경 시 스트림 트랙 제어
    useEffect(() => {
        if (stream) {
            stream.getAudioTracks().forEach(track => {
                track.enabled = isMicEnabled;
            });
            stream.getVideoTracks().forEach(track => {
                track.enabled = isCameraEnabled;
            });
        }
    }, [isMicEnabled, isCameraEnabled, stream]);

    const handleEnterRoom = () => {
        if (!reservationId) return;

        // 스트림 정리 (실제 방으로 넘어가기 전 해제)
        if (stream) {
            stream.getTracks().forEach(track => track.stop());
        }

        // 실제 상담 방으로 이동하며 상태 전달
        navigate(`/consultation-room/${roomId}`, {
            state: {
                initialMic: isMicEnabled,
                initialCamera: isCameraEnabled,
            },
        });
    };

    const handleBack = () => {
        navigate("/mypage/consultations");
    };

    if (!reservationId) {
        return (
            <div className="flex items-center justify-center h-screen bg-gray-950 text-white">
                <div className="text-center">
                    <h2 className="text-xl font-bold mb-4">잘못된 접근입니다.</h2>
                    <Button onClick={handleBack} variant="secondary">돌아가기</Button>
                </div>
            </div>
        );
    }

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-950 text-white p-4">
            <div className="w-full max-w-4xl flex flex-col items-center gap-8">

                <div className="text-center space-y-2">
                    <h1 className="text-3xl font-bold text-white">
                        상담 대기방
                    </h1>
                    <p className="text-gray-400">
                        입장 전 카메라와 마이크 상태를 확인해주세요.
                    </p>
                </div>

                {/* 비디오 미리보기 영역 */}
                <div className="relative aspect-video w-full max-w-2xl bg-gray-800 rounded-lg overflow-hidden shadow-2xl border border-gray-700">
                    {error ? (
                        <div className="absolute inset-0 flex flex-col items-center justify-center gap-4 text-red-500 bg-gray-900/90 text-center p-6">
                            <span className="text-4xl">⚠️</span>
                            <p>{error}</p>
                            <Button onClick={() => window.location.reload()} variant="outline" className="mt-2 text-white border-white/20 hover:bg-white/10">
                                다시 시도
                            </Button>
                        </div>
                    ) : (
                        <>
                            <video
                                ref={videoRef}
                                autoPlay
                                playsInline
                                muted // 로컬 미리보기는 항상 음소거 (하울링 방지)
                                className={`w-full h-full object-cover transform scale-x-[-1] transition-opacity duration-300 ${isCameraEnabled ? 'opacity-100' : 'opacity-0'}`}
                            />

                            {!isCameraEnabled && (
                                <div className="absolute inset-0 flex items-center justify-center bg-gray-800">
                                    <div className="text-gray-500">카메라 꺼짐</div>
                                </div>
                            )}

                            {/* 상태 라벨 */}
                            <div className="absolute bottom-4 left-4 bg-black/60 text-white px-3 py-1 rounded text-sm">
                                나 (대기 중)
                            </div>
                        </>
                    )}
                </div>

                {/* 컨트롤 버튼 (ConsultationRoomPage 스타일 적용) */}
                <div className="bg-gray-800 rounded-full px-6 py-4 shadow-2xl border border-gray-700 flex items-center gap-4">
                    {/* 마이크 버튼 */}
                    <button
                        className={`p-3 rounded-full transition-colors ${isMicEnabled ? "bg-gray-700 hover:bg-gray-600" : "bg-red-600 hover:bg-red-700"}`}
                        onClick={() => setIsMicEnabled(!isMicEnabled)}
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
                        onClick={() => setIsCameraEnabled(!isCameraEnabled)}
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
                </div>

                {/* 입장 버튼 */}
                <div className="flex gap-4 w-full max-w-sm mt-4">
                    <Button
                        className="flex-1 h-12 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
                        onClick={handleEnterRoom}
                        disabled={!!error}
                    >
                        상담 입장하기
                    </Button>
                    <Button
                        variant="ghost"
                        className="h-12 px-6 text-gray-400 hover:text-white hover:bg-gray-800 rounded-lg"
                        onClick={handleBack}
                    >
                        취소
                    </Button>
                </div>

            </div>
        </div>
    );
}

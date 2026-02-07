import { useRef, useEffect, useState, useCallback } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog";
import { Check, X, AlertCircle } from "lucide-react";
import { FaceMesh } from "@mediapipe/face_mesh";
import type { Results as FaceMeshResults } from "@mediapipe/face_mesh";
import { cn } from "@/lib/utils";

export type CaptureMode = "left" | "front" | "right";

interface WebcamModalProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onCapture: (file: File, mode: CaptureMode) => void;
}

export function WebcamModal({ open, onOpenChange, onCapture }: WebcamModalProps) {
    const videoRef = useRef<HTMLVideoElement>(null);
    const requestRef = useRef<number>(0);
    const [activeMode, setActiveMode] = useState<CaptureMode>("left");
    const activeModeRef = useRef<CaptureMode>("left"); // Ref to track current mode without closure staleness
    const [cameraError, setCameraError] = useState<string | null>(null);
    const [guidance, setGuidance] = useState<string>("카메라를 응시해주세요");
    const [isAngleCorrect, setIsAngleCorrect] = useState(false);
    const [isWarning, setIsWarning] = useState(false);

    // Update ref when state changes
    useEffect(() => {
        activeModeRef.current = activeMode;
    }, [activeMode]);

    // MediaPipe Ref
    const faceMeshRef = useRef<FaceMesh | null>(null);
    const streamRef = useRef<MediaStream | null>(null);

    // Initialize MediaPipe    // Clean up when modal closes
    useEffect(() => {
        if (!open) {
            return;
        }

        const faceMesh = new FaceMesh({
            locateFile: (file) => {
                return `https://cdn.jsdelivr.net/npm/@mediapipe/face_mesh/${file}`;
            },
        });

        faceMesh.setOptions({
            maxNumFaces: 1,
            refineLandmarks: true,
            minDetectionConfidence: 0.5,
            minTrackingConfidence: 0.5,
        });

        faceMesh.onResults(onResults);
        faceMeshRef.current = faceMesh;

        // Start Camera directly
        startCamera();

        return () => {
            stopCamera();
            if (faceMeshRef.current) {
                faceMeshRef.current.close();
                faceMeshRef.current = null;
            }
        };
    }, [open]);

    const startCamera = async () => {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({
                video: { facingMode: "user", width: 640, height: 480 }
            });

            streamRef.current = stream;
            if (videoRef.current) {
                videoRef.current.srcObject = stream;
                // Wait for video to load enough data to play
                videoRef.current.onloadedmetadata = () => {
                    videoRef.current?.play();
                    processVideo(); // Start processing loop
                };
            }
        } catch (err) {
            console.error("Camera start error", err);
            setCameraError("카메라를 실행할 수 없습니다. 권한을 확인해주세요.");
        }
    };

    const stopCamera = () => {
        if (streamRef.current) {
            streamRef.current.getTracks().forEach(track => track.stop());
            streamRef.current = null;
        }
        if (requestRef.current) {
            cancelAnimationFrame(requestRef.current);
        }
    };

    const processVideo = async () => {
        if (!videoRef.current || !faceMeshRef.current || !open) return;

        if (videoRef.current.readyState >= 2) { // HAVE_CURRENT_DATA
            try {
                await faceMeshRef.current.send({ image: videoRef.current });
            } catch (e) {
                // Ignore keyframes if busy
            }
        }
        requestRef.current = requestAnimationFrame(processVideo);
    };

    const onResults = useCallback((results: FaceMeshResults) => {
        if (!open) return;

        // Filter out if no face
        if (!results.multiFaceLandmarks || results.multiFaceLandmarks.length === 0) {
            setGuidance("얼굴이 잘 보이게 해주세요");
            setIsAngleCorrect(false);
            setIsWarning(false);
            return;
        }

        const landmarks = results.multiFaceLandmarks[0];

        const nose = landmarks[1];
        const leftEar = landmarks[234];
        const rightEar = landmarks[454];

        // Safety check
        if (!nose || !leftEar || !rightEar) return;

        const dist = Math.abs(leftEar.x - rightEar.x);
        const mid = (leftEar.x + rightEar.x) / 2;
        const offset = nose.x - mid;
        const yawRatio = offset / dist;

        let isGood = false;
        let isWarn = false;
        let msg = "";

        // Use REF here to get current mode!
        const currentMode = activeModeRef.current;

        switch (currentMode) {
            case "front":
                if (Math.abs(yawRatio) < 0.15) {
                    isGood = true;
                    msg = "좋습니다! (정면)";
                } else {
                    msg = yawRatio > 0 ? "고개를 오른쪽으로 돌려주세요" : "고개를 왼쪽으로 돌려주세요";
                }
                break;
            case "left":
                // Target: Show LEFT Cheek -> User turns head RIGHT (Negative Yaw)
                if (yawRatio < -0.45) {
                    isGood = false;
                    isWarn = true; // Set warning
                    msg = "너무 많이 돌렸습니다. 정면을 조금 봐주세요";
                } else if (yawRatio < -0.20) {
                    isGood = true;
                    msg = "좋습니다! (좌측면)";
                } else {
                    isGood = false;
                    msg = "고개를 오른쪽으로 돌려주세요";
                }
                break;
            case "right":
                // Target: Show RIGHT Cheek -> User turns head LEFT (Positive Yaw)
                if (yawRatio > 0.45) {
                    isGood = false;
                    isWarn = true; // Set warning
                    msg = "너무 많이 돌렸습니다. 정면을 조금 봐주세요";
                } else if (yawRatio > 0.20) {
                    isGood = true;
                    msg = "좋습니다! (우측면)";
                } else {
                    isGood = false;
                    msg = "고개를 왼쪽으로 돌려주세요";
                }
                break;
        }

        setIsAngleCorrect(isGood);
        setIsWarning(isWarn);
        setGuidance(msg);

    }, [open]); // Removed activeMode dependency since we use ref

    const capture = useCallback(() => {
        if (!videoRef.current) return;

        const video = videoRef.current;
        const canvas = document.createElement("canvas");
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        const ctx = canvas.getContext("2d");
        if (!ctx) return;

        ctx.translate(canvas.width, 0);
        ctx.scale(-1, 1);
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

        canvas.toBlob((blob) => {
            if (blob) {
                const file = new File([blob], `capture_${activeMode}_${Date.now()}.jpg`, { type: "image/jpeg" });
                onCapture(file, activeMode);
            }
        }, "image/jpeg", 0.95);
    }, [activeMode, onCapture]);

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-4xl bg-black/95 p-0 text-white border-none overflow-hidden sm:rounded-2xl max-h-[90vh]">
                <div className="flex h-full flex-col md:flex-row">
                    {/* Sidebar / Topbar for Controls (Mobile adaptive) */}
                    <div className="flex w-full md:w-64 flex-col bg-zinc-900 p-6 md:border-r border-white/10 order-2 md:order-1">
                        <DialogHeader className="mb-6 text-left">
                            <DialogTitle className="text-xl font-bold">얼굴 촬영</DialogTitle>
                            <DialogDescription className="text-zinc-400">
                                가이드에 맞춰 촬영해주세요.
                            </DialogDescription>
                        </DialogHeader>

                        {/* Mode Selector */}
                        <div className="space-y-2 mb-8">
                            <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">촬영 모드</label>
                            <div className="flex flex-col gap-2">
                                <button
                                    onClick={() => setActiveMode("left")}
                                    className={cn(
                                        "flex items-center justify-between rounded-lg p-3 text-sm font-medium transition-all",
                                        activeMode === "left" ? "bg-primary-500 text-white" : "bg-zinc-800 text-zinc-400 hover:bg-zinc-700 hover:text-white"
                                    )}
                                >
                                    <span>좌측면 (30°)</span>
                                </button>
                                <button
                                    onClick={() => setActiveMode("front")}
                                    className={cn(
                                        "flex items-center justify-between rounded-lg p-3 text-sm font-medium transition-all",
                                        activeMode === "front" ? "bg-primary-500 text-white" : "bg-zinc-800 text-zinc-400 hover:bg-zinc-700 hover:text-white"
                                    )}
                                >
                                    <span>정면 (0°)</span>
                                </button>
                                <button
                                    onClick={() => setActiveMode("right")}
                                    className={cn(
                                        "flex items-center justify-between rounded-lg p-3 text-sm font-medium transition-all",
                                        activeMode === "right" ? "bg-primary-500 text-white" : "bg-zinc-800 text-zinc-400 hover:bg-zinc-700 hover:text-white"
                                    )}
                                >
                                    <span>우측면 (30°)</span>
                                </button>
                            </div>
                        </div>

                        {/* Instructions */}
                        <div className="mt-auto rounded-xl bg-zinc-800/50 p-4">
                            <div className="flex gap-3">
                                <div className="flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-blue-500/20 text-blue-400">
                                    <div className="h-1.5 w-1.5 rounded-full bg-current" />
                                </div>
                                <p className="text-xs leading-relaxed text-zinc-300">
                                    {activeMode === "left" && "고개를 오른쪽으로 돌려 왼쪽 뺨이 잘 보이게 해주세요."}
                                    {activeMode === "front" && "카메라를 정면으로 응시해주세요."}
                                    {activeMode === "right" && "고개를 왼쪽으로 돌려 오른쪽 뺨이 잘 보이게 해주세요."}
                                </p>
                            </div>
                        </div>
                    </div>

                    {/* Main Camera Area */}
                    <div className="relative flex-1 bg-black order-1 md:order-2 flex flex-col">
                        {/* Close Button */}
                        <div className="absolute top-4 right-4 z-50">
                            <button onClick={() => onOpenChange(false)} className="rounded-full bg-black/40 p-2 text-white/70 backdrop-blur-md hover:bg-black/60 hover:text-white transition-colors">
                                <X className="h-5 w-5" />
                            </button>
                        </div>

                        {/* Video Container */}
                        <div className="relative h-full w-full overflow-hidden flex items-center justify-center">
                            {cameraError && (
                                <div className="absolute inset-0 z-20 flex flex-col items-center justify-center bg-zinc-900 p-8 text-center">
                                    <p className="text-red-400 mb-4">{cameraError}</p>
                                    <button onClick={() => window.location.reload()} className="px-4 py-2 bg-zinc-800 rounded-lg text-sm">페이지 새로고침</button>
                                </div>
                            )}

                            <video
                                ref={videoRef}
                                className="h-full w-full object-cover transform scale-x-[-1]" // Mirror preview
                                playsInline
                                muted
                                autoPlay // Ensure autoplay
                            />

                            {/* Overlays / Guides */}
                            <div className="absolute inset-0 pointer-events-none">
                                {/* Angle Feedback Border */}
                                <div className={cn(
                                    "absolute inset-0 border-[6px] transition-colors duration-300",
                                    isAngleCorrect ? "border-green-500/50" :
                                        isWarning ? "border-red-500/50" : "border-transparent"
                                )} />

                                {/* Center Guide Frame */}
                                <div className="absolute inset-0 flex items-center justify-center">
                                    <div className={cn(
                                        "w-[280px] h-[380px] rounded-[40%] border-2 border-dashed transition-all duration-500 opacity-50",
                                        isAngleCorrect ? "border-green-400 scale-105" :
                                            isWarning ? "border-red-400" : "border-white/30"
                                    )} />
                                </div>

                                {/* Status Text Toast */}
                                <div className="absolute top-8 left-1/2 -translate-x-1/2">
                                    <div className={cn(
                                        "px-6 py-2 rounded-full backdrop-blur-md transition-all duration-300 flex items-center gap-2 shadow-xl",
                                        isAngleCorrect ? "bg-green-500/90 text-white" :
                                            isWarning ? "bg-red-500/90 text-white" : "bg-black/60 text-zinc-200"
                                    )}>
                                        {isAngleCorrect ? <Check className="h-4 w-4" /> :
                                            isWarning ? <AlertCircle className="h-4 w-4" /> : null}
                                        <span className="text-sm font-medium whitespace-nowrap">{guidance}</span>
                                    </div>
                                </div>
                            </div>

                            {/* Capture Button (Floating at bottom for mobile/desktop unification) */}
                            <div className="absolute bottom-10 left-0 right-0 flex items-center justify-center z-30">
                                <button
                                    onClick={capture}
                                    disabled={!!cameraError}
                                    className={cn(
                                        "group relative h-20 w-20 rounded-full border-4 flex items-center justify-center transition-all duration-300",
                                        isAngleCorrect ? "border-green-500 bg-green-500/20 hover:bg-green-500/30" : "border-white/80 bg-white/10 hover:bg-white/20"
                                    )}
                                >
                                    <div className={cn(
                                        "h-16 w-16 rounded-full transition-all duration-200",
                                        isAngleCorrect ? "bg-green-500 scale-90 group-hover:scale-100" : "bg-white scale-90 group-hover:scale-95"
                                    )} />
                                </button>

                                {/* Helper text below button */}
                                <span className="absolute -bottom-8 text-xs text-white/50 font-medium tracking-wide">
                                    {isAngleCorrect ? "지금 촬영하세요!" : "촬영"}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    );
}

import { useState, useRef, useEffect, useCallback, useMemo } from "react";
import { Camera, CameraOff } from "lucide-react";
import { useFaceMask, type MaskType } from "@/hooks/useFaceMask";

interface FaceCameraSectionProps {
  selectedArea: string;
  onAreaSelect: (area: string) => void;
}

type FaceArea = {
  id: string;
  label: string;
};

const faceAreas: FaceArea[] = [
  { id: "t-zone", label: "T-zone" },
  { id: "u-zone", label: "U-zone" },
  { id: "nose", label: "Nose zone" },
  { id: "apple", label: "Apple zone" },
];

export function FaceCameraSection({ selectedArea, onAreaSelect }: FaceCameraSectionProps) {
  const [cameraOn, setCameraOn] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>("");
  const [videoElement, setVideoElement] = useState<HTMLVideoElement | null>(null);
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const streamRef = useRef<MediaStream | null>(null);

  const maskType = useMemo<MaskType>(() => {
    switch (selectedArea) {
      case "t-zone":
        return "T-zone";
      case "u-zone":
        return "U-zone";
      case "nose":
        return "Nose zone";
      case "apple":
        return "Apple zone";
      default:
        return null;
    }
  }, [selectedArea]);

  useFaceMask(videoElement, canvasRef, maskType);

  // Stop camera
  const stopCamera = useCallback(() => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => {
        track.stop();
      });
      streamRef.current = null;
    }

    if (videoRef.current) {
      videoRef.current.srcObject = null;
    }

    setCameraOn(false);
    setError("");
    setVideoElement(null);
  }, []);

  // Start camera
  const startCamera = useCallback(async () => {
    try {
      setError("");
      setIsLoading(true);

      // First set camera on to ensure video element is in DOM
      setCameraOn(true);

      // Wait for React to render
      await new Promise((resolve) => setTimeout(resolve, 100));

      const stream = await navigator.mediaDevices.getUserMedia({
        video: {
          width: { ideal: 1280 },
          height: { ideal: 720 },
          facingMode: "user",
        },
        audio: false,
      });

      if (!videoRef.current) {
        throw new Error("비디오 요소를 찾을 수 없습니다");
      }

      videoRef.current.srcObject = stream;
      streamRef.current = stream;
      setVideoElement(videoRef.current);

      // Wait for video to be ready and play
      await new Promise<void>((resolve, reject) => {
        if (!videoRef.current) {
          reject(new Error("비디오 요소가 손실되었습니다"));
          return;
        }

        videoRef.current.onloadedmetadata = async () => {
          try {
            await videoRef.current?.play();
            resolve();
          } catch (err) {
            reject(err);
          }
        };

        // Timeout after 5 seconds
        setTimeout(() => reject(new Error("Timeout")), 5000);
      });
    } catch (err) {
      // Stop any active streams
      if (streamRef.current) {
        streamRef.current.getTracks().forEach((track) => track.stop());
        streamRef.current = null;
      }

      if (err instanceof Error) {
        if (err.name === "NotAllowedError") {
          setError("카메라 접근 권한이 거부되었습니다.");
        } else if (err.name === "NotFoundError") {
          setError("카메라를 찾을 수 없습니다.");
        } else if (err.message.includes("Timeout")) {
          setError("카메라 로딩 시간이 초과되었습니다.");
        } else {
          setError(`카메라를 시작할 수 없습니다: ${err.message}`);
        }
      }
      setCameraOn(false);
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Toggle camera on/off
  const toggleCamera = useCallback(() => {
    if (cameraOn) {
      stopCamera();
    } else {
      startCamera();
    }
  }, [cameraOn, startCamera, stopCamera]);

  // Handle area selection
  const handleAreaSelect = (areaId: string) => {
    onAreaSelect(areaId);
  };

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      if (streamRef.current) {
        streamRef.current.getTracks().forEach((track) => track.stop());
      }
    };
  }, []);

  return (
    <div className="flex flex-col items-center gap-6">
      <h3 className="text-lg font-semibold text-text-primary">부위를 선택하세요</h3>

      {/* Camera View Container */}
      <div className="relative w-full max-w-[400px] aspect-[3/4] bg-muted/30 rounded-2xl border-2 border-border overflow-hidden">
        {/* Video section - Always in DOM, visibility controlled by CSS */}
        <div className={`relative w-full h-full bg-black ${cameraOn && !isLoading ? "block" : "hidden"}`}>
          {/* Video element */}
          <video ref={videoRef} autoPlay playsInline muted className="absolute inset-0 w-full h-full object-cover" style={{ transform: "scaleX(-1)" }} />

          {/* Canvas overlay for future FaceMesh */}
          <canvas ref={canvasRef} className="absolute inset-0 w-full h-full pointer-events-none" style={{ transform: "scaleX(-1)" }} />

          {/* Camera controls overlay */}
          <div className="absolute bottom-4 left-1/2 -translate-x-1/2 z-10">
            <button
              onClick={toggleCamera}
              disabled={isLoading}
              className="inline-flex items-center gap-2 px-4 py-2 bg-dark-bg/80 backdrop-blur-sm text-white rounded-lg font-medium hover:bg-dark-bg/90 transition-colors shadow-lg text-sm disabled:opacity-50"
            >
              <CameraOff className="h-4 w-4" />
              카메라 끄기
            </button>
          </div>
        </div>

        {/* Camera OFF state or Loading - Overlays when needed */}
        <div className={`absolute inset-0 flex flex-col items-center justify-center p-6 bg-background ${!cameraOn || isLoading ? "flex" : "hidden"}`}>
          <div className="mb-6 h-24 w-24 rounded-full bg-muted flex items-center justify-center">
            {isLoading ? <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500"></div> : <CameraOff className="h-12 w-12 text-text-secondary" />}
          </div>
          <p className="text-center text-sm text-text-secondary mb-6 max-w-[280px]">{isLoading ? "카메라를 켜는 중..." : "카메라를 켜고 얼굴에 직접 제품을 적용해보세요"}</p>
          <button
            onClick={toggleCamera}
            disabled={isLoading}
            className="inline-flex items-center gap-2 px-6 py-3 bg-primary-500 text-white rounded-xl font-medium hover:bg-primary-400 transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Camera className="h-5 w-5" />
            {isLoading ? "로딩 중..." : "카메라 켜기"}
          </button>
        </div>
      </div>

      {/* Error message */}
      {error && (
        <div className="w-full max-w-[400px] p-3 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-sm text-red-600 text-center">{error}</p>
        </div>
      )}

      {/* Face Area Selection Buttons */}
      <div className="w-full max-w-[400px]">
        <p className="text-sm font-medium text-text-secondary mb-3 text-center">적용할 부위 선택</p>
        <div className="grid grid-cols-2 gap-3">
          {faceAreas.map((area) => {
            const isSelected = selectedArea === area.id;

            return (
              <button
                key={area.id}
                onClick={() => handleAreaSelect(area.id)}
                className={`
                  px-4 py-3 rounded-xl text-sm font-medium
                  transition-all duration-300
                  ${isSelected ? "bg-primary-500 text-white shadow-md scale-[1.02]" : "bg-background border-2 border-border text-text-primary hover:border-primary-300 hover:bg-primary-100/30"}
                `}
                aria-label={`${area.label} 선택`}
              >
                {area.label}
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
}

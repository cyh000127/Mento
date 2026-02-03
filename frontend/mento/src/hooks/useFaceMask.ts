import { useEffect, useRef } from "react";
import { FaceMesh } from "@mediapipe/face_mesh";
import type { Results, NormalizedLandmarkList } from "@mediapipe/face_mesh";

export type MaskType = "T-zone" | "U-zone" | "Nose zone" | "Apple zone" | null;

interface MaskConfig {
  color: string;
  drawMask: (ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, canvas: HTMLCanvasElement) => void;
}

/**
 * T-zone 마스크 그리기 (이마 + 코)
 */
function drawTZoneMask(ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, canvas: HTMLCanvasElement) {
  // 이마 가로바 (Top Bar)
  const foreheadTop = landmarks[10];
  const foreheadLeft = landmarks[103];
  const foreheadRight = landmarks[332];

  // 가로바 하단 경계
  const templeLeft = landmarks[66];
  const templeRight = landmarks[296];

  // T의 꺾이는 부분 (눈썹 안쪽)
  const browInnerLeft = landmarks[107];
  const browInnerRight = landmarks[336];

  // 콧대
  const noseBridgeLeft = landmarks[193];
  const noseBridgeRight = landmarks[417];
  const noseTip = landmarks[4];

  ctx.beginPath();

  // 시계 방향으로 T 모양 그리기
  ctx.moveTo(foreheadLeft.x * canvas.width, foreheadLeft.y * canvas.height);
  ctx.lineTo(foreheadTop.x * canvas.width, foreheadTop.y * canvas.height);
  ctx.lineTo(foreheadRight.x * canvas.width, foreheadRight.y * canvas.height);
  ctx.lineTo(templeRight.x * canvas.width, templeRight.y * canvas.height);
  ctx.lineTo(browInnerRight.x * canvas.width, browInnerRight.y * canvas.height);
  ctx.lineTo(noseBridgeRight.x * canvas.width, noseBridgeRight.y * canvas.height);
  ctx.lineTo(noseTip.x * canvas.width, noseTip.y * canvas.height);
  ctx.lineTo(noseBridgeLeft.x * canvas.width, noseBridgeLeft.y * canvas.height);
  ctx.lineTo(browInnerLeft.x * canvas.width, browInnerLeft.y * canvas.height);
  ctx.lineTo(templeLeft.x * canvas.width, templeLeft.y * canvas.height);

  ctx.closePath();
  ctx.fill();
}

/**
 * U-zone 마스크 그리기 (볼 + 턱)
 */
function drawUZoneMask(ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, canvas: HTMLCanvasElement) {
  // 왼쪽 볼 영역
  const leftCheekTop = landmarks[116];
  const leftCheekOuter = landmarks[123];
  const leftCheekBottom = landmarks[147];

  // 오른쪽 볼 영역
  const rightCheekTop = landmarks[345];
  const rightCheekOuter = landmarks[352];
  const rightCheekBottom = landmarks[376];

  // 턱 영역
  const chinLeft = landmarks[172];
  const chinCenter = landmarks[152];
  const chinRight = landmarks[397];

  ctx.beginPath();

  // 왼쪽 볼에서 시작
  ctx.moveTo(leftCheekTop.x * canvas.width, leftCheekTop.y * canvas.height);
  ctx.lineTo(leftCheekOuter.x * canvas.width, leftCheekOuter.y * canvas.height);
  ctx.lineTo(leftCheekBottom.x * canvas.width, leftCheekBottom.y * canvas.height);
  ctx.lineTo(chinLeft.x * canvas.width, chinLeft.y * canvas.height);
  ctx.lineTo(chinCenter.x * canvas.width, chinCenter.y * canvas.height);
  ctx.lineTo(chinRight.x * canvas.width, chinRight.y * canvas.height);
  ctx.lineTo(rightCheekBottom.x * canvas.width, rightCheekBottom.y * canvas.height);
  ctx.lineTo(rightCheekOuter.x * canvas.width, rightCheekOuter.y * canvas.height);
  ctx.lineTo(rightCheekTop.x * canvas.width, rightCheekTop.y * canvas.height);

  ctx.closePath();
  ctx.fill();
}

/**
 * Nose zone 마스크 그리기 (코 주변)
 */
function drawNoseZoneMask(ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, canvas: HTMLCanvasElement) {
  // 삼각형 코 마스크 포인트
  const noseTop = landmarks[6]; // 코 브릿지 상단
  const noseLeft = landmarks[219]; // 왼쪽 코 날개
  const noseRight = landmarks[439]; // 오른쪽 코 날개

  ctx.beginPath();
  ctx.moveTo(noseTop.x * canvas.width, noseTop.y * canvas.height);
  ctx.lineTo(noseRight.x * canvas.width, noseRight.y * canvas.height);
  ctx.lineTo(noseLeft.x * canvas.width, noseLeft.y * canvas.height);

  ctx.closePath();
  ctx.fill();
}

/**
 * Apple zone 마스크 그리기 (사과 존 - 원형)
 */
function drawAppleZoneMask(ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, canvas: HTMLCanvasElement) {
  // 왼쪽 애플 존 기준 포인트
  const leftTop = landmarks[50];
  const leftBottom = landmarks[36];

  // 오른쪽 애플 존 기준 포인트
  const rightTop = landmarks[280];
  const rightBottom = landmarks[266];

  const drawCircle = (top: any, bottom: any) => {
    const centerX = ((top.x + bottom.x) / 2) * canvas.width;
    const centerY = ((top.y + bottom.y) / 2) * canvas.height;

    const radius = Math.hypot((top.x - bottom.x) * canvas.width, (top.y - bottom.y) * canvas.height) * 0.6; // 크기 조절 계수

    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, 0, Math.PI * 2);
    ctx.closePath();
    ctx.fill();
  };

  // 왼쪽 / 오른쪽 애플 존 그리기
  drawCircle(leftTop, leftBottom);
  drawCircle(rightTop, rightBottom);
}

/**
 * 마스크 타입별 설정
 */
const MASK_CONFIGS: Record<NonNullable<MaskType>, MaskConfig> = {
  "T-zone": {
    color: "rgba(255, 200, 0, 0.35)", // 노란색
    drawMask: drawTZoneMask,
  },
  "U-zone": {
    color: "rgba(100, 200, 255, 0.35)", // 파란색
    drawMask: drawUZoneMask,
  },
  "Nose zone": {
    color: "rgba(100, 255, 150, 0.35)", // 초록색
    drawMask: drawNoseZoneMask,
  },
  "Apple zone": {
    color: "rgba(255, 100, 100, 0.35)", // 빨간색
    drawMask: drawAppleZoneMask,
  },
};

/**
 * MediaPipe FaceMesh를 사용하여 얼굴 마스크 오버레이를 그리는 범용 커스텀 훅
 *
 * @param videoElement - LiveKit video element
 * @param canvasRef - 오버레이를 그릴 canvas의 ref
 * @param maskType - 마스크 타입 ('T-zone' | 'U-zone' | 'Nose zone' | 'Apple zone' | null)
 */
export function useFaceMask(videoElement: HTMLVideoElement | null, canvasRef: React.RefObject<HTMLCanvasElement | null>, maskType: MaskType) {
  const faceMeshRef = useRef<FaceMesh | null>(null);
  const animationFrameRef = useRef<number | null>(null);
  const maskTypeRef = useRef<MaskType>(maskType);

  useEffect(() => {
    maskTypeRef.current = maskType;
    if (!maskType && canvasRef.current) {
      const ctx = canvasRef.current.getContext("2d");
      if (ctx) {
        ctx.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);
      }
    }
  }, [maskType, canvasRef]);

  useEffect(() => {
    // 마스크가 선택되지 않았거나 필수 요소가 없으면 종료
    if (!videoElement || !canvasRef.current) {
      return;
    }

    const canvas = canvasRef.current;
    const ctx = canvas.getContext("2d");

    if (!ctx) {
      console.error("Canvas 2D context를 가져올 수 없습니다.");
      return;
    }

    console.log(`🎭 FaceMesh 초기화 시작`);

    // MediaPipe FaceMesh 초기화
    const faceMesh = new FaceMesh({
      locateFile: (file) => {
        return `https://cdn.jsdelivr.net/npm/@mediapipe/face_mesh/${file}`;
      },
    });

    faceMesh.setOptions({
      maxNumFaces: 1, // 단일 얼굴만 감지
      refineLandmarks: true, // 더 정확한 랜드마크
      minDetectionConfidence: 0.5,
      minTrackingConfidence: 0.5,
    });

    /**
     * FaceMesh 결과 처리 및 마스크 그리기
     */
    faceMesh.onResults((results: Results) => {
      if (!canvas || !ctx) return;

      const videoWidth = videoElement.videoWidth || videoElement.clientWidth;
      const videoHeight = videoElement.videoHeight || videoElement.clientHeight;

      if (!videoWidth || !videoHeight) {
        return;
      }

      // 캔버스 크기를 비디오 크기와 동기화
      if (canvas.width !== videoWidth || canvas.height !== videoHeight) {
        canvas.width = videoWidth;
        canvas.height = videoHeight;
      }

      // 이전 프레임 지우기
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      const activeMaskType = maskTypeRef.current;
      if (!activeMaskType) {
        return;
      }

      // 얼굴이 감지되지 않으면 리턴
      if (!results.multiFaceLandmarks || results.multiFaceLandmarks.length === 0) {
        return;
      }

      const landmarks = results.multiFaceLandmarks[0];
      const config = MASK_CONFIGS[activeMaskType];

      if (!config) return;

      // 마스크 색상 설정
      ctx.fillStyle = config.color;

      // 마스크 그리기
      config.drawMask(ctx, landmarks, canvas);
    });

    faceMeshRef.current = faceMesh;

    /**
     * requestAnimationFrame을 사용한 실시간 처리 루프
     */
    let isCancelled = false;
    let isProcessing = false;

    const processFrame = async () => {
      if (isCancelled) return;
      if (!videoElement || videoElement.readyState < 2) {
        animationFrameRef.current = requestAnimationFrame(processFrame);
        return;
      }

      try {
        if (isProcessing) {
          animationFrameRef.current = requestAnimationFrame(processFrame);
          return;
        }
        isProcessing = true;
        await faceMesh.send({ image: videoElement });
      } catch (error) {
        if (!isCancelled) {
          console.error("FaceMesh 처리 중 오류:", error);
        }
      } finally {
        isProcessing = false;
      }

      animationFrameRef.current = requestAnimationFrame(processFrame);
    };

    const startProcessing = () => {
      console.log(`✅ FaceMesh 처리 시작`, {
        videoWidth: videoElement.videoWidth,
        videoHeight: videoElement.videoHeight,
      });
      processFrame();
    };

    if (videoElement.readyState >= 2) {
      startProcessing();
    } else {
      videoElement.addEventListener("loadeddata", startProcessing);
    }

    // 클린업
    return () => {
      console.log(`🧹 FaceMesh 클린업`);
      isCancelled = true;

      if (animationFrameRef.current) {
        cancelAnimationFrame(animationFrameRef.current);
        animationFrameRef.current = null;
      }

      if (faceMeshRef.current) {
        try {
          faceMeshRef.current.close();
        } catch (error) {
          console.warn("⚠️ FaceMesh 종료 중 오류:", error);
        } finally {
          faceMeshRef.current = null;
        }
      }

      videoElement.removeEventListener("loadeddata", startProcessing);

      // 캔버스 초기화
      if (ctx) {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
      }
    };
  }, [videoElement, canvasRef]);

  return faceMeshRef;
}

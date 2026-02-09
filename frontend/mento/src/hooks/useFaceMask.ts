import { useEffect, useRef } from "react";
import { FaceMesh } from "@mediapipe/face_mesh";
import type { Results, NormalizedLandmarkList } from "@mediapipe/face_mesh";

export type MaskType = "T-zone" | "U-zone" | "Nose zone" | "Apple zone" | null;

type Point = { x: number; y: number };
type LandmarkMapper = (landmark: { x: number; y: number }) => Point;

interface MaskConfig {
  color: string;
  drawMask: (ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, mapPoint: LandmarkMapper) => void;
}

/**
 * T-zone 마스크 그리기 (이마 + 코)
 */
function drawTZoneMask(ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, mapPoint: LandmarkMapper) {
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
  const foreheadLeftPoint = mapPoint(foreheadLeft);
  const foreheadTopPoint = mapPoint(foreheadTop);
  const foreheadRightPoint = mapPoint(foreheadRight);
  const templeRightPoint = mapPoint(templeRight);
  const browInnerRightPoint = mapPoint(browInnerRight);
  const noseBridgeRightPoint = mapPoint(noseBridgeRight);
  const noseTipPoint = mapPoint(noseTip);
  const noseBridgeLeftPoint = mapPoint(noseBridgeLeft);
  const browInnerLeftPoint = mapPoint(browInnerLeft);
  const templeLeftPoint = mapPoint(templeLeft);

  ctx.moveTo(foreheadLeftPoint.x, foreheadLeftPoint.y);
  ctx.lineTo(foreheadTopPoint.x, foreheadTopPoint.y);
  ctx.lineTo(foreheadRightPoint.x, foreheadRightPoint.y);
  ctx.lineTo(templeRightPoint.x, templeRightPoint.y);
  ctx.lineTo(browInnerRightPoint.x, browInnerRightPoint.y);
  ctx.lineTo(noseBridgeRightPoint.x, noseBridgeRightPoint.y);
  ctx.lineTo(noseTipPoint.x, noseTipPoint.y);
  ctx.lineTo(noseBridgeLeftPoint.x, noseBridgeLeftPoint.y);
  ctx.lineTo(browInnerLeftPoint.x, browInnerLeftPoint.y);
  ctx.lineTo(templeLeftPoint.x, templeLeftPoint.y);

  ctx.closePath();
  ctx.fill();
}

/**
 * U-zone 마스크 그리기 (볼 + 턱)
 */
function drawUZoneMask(ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, mapPoint: LandmarkMapper) {
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
  const leftCheekTopPoint = mapPoint(leftCheekTop);
  const leftCheekOuterPoint = mapPoint(leftCheekOuter);
  const leftCheekBottomPoint = mapPoint(leftCheekBottom);
  const chinLeftPoint = mapPoint(chinLeft);
  const chinCenterPoint = mapPoint(chinCenter);
  const chinRightPoint = mapPoint(chinRight);
  const rightCheekBottomPoint = mapPoint(rightCheekBottom);
  const rightCheekOuterPoint = mapPoint(rightCheekOuter);
  const rightCheekTopPoint = mapPoint(rightCheekTop);

  ctx.moveTo(leftCheekTopPoint.x, leftCheekTopPoint.y);
  ctx.lineTo(leftCheekOuterPoint.x, leftCheekOuterPoint.y);
  ctx.lineTo(leftCheekBottomPoint.x, leftCheekBottomPoint.y);
  ctx.lineTo(chinLeftPoint.x, chinLeftPoint.y);
  ctx.lineTo(chinCenterPoint.x, chinCenterPoint.y);
  ctx.lineTo(chinRightPoint.x, chinRightPoint.y);
  ctx.lineTo(rightCheekBottomPoint.x, rightCheekBottomPoint.y);
  ctx.lineTo(rightCheekOuterPoint.x, rightCheekOuterPoint.y);
  ctx.lineTo(rightCheekTopPoint.x, rightCheekTopPoint.y);

  ctx.closePath();
  ctx.fill();
}

/**
 * Nose zone 마스크 그리기 (코 주변)
 */
function drawNoseZoneMask(ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, mapPoint: LandmarkMapper) {
  // 삼각형 코 마스크 포인트
  const noseTop = landmarks[6]; // 코 브릿지 상단
  const noseLeft = landmarks[219]; // 왼쪽 코 날개
  const noseRight = landmarks[439]; // 오른쪽 코 날개

  ctx.beginPath();
  const noseTopPoint = mapPoint(noseTop);
  const noseRightPoint = mapPoint(noseRight);
  const noseLeftPoint = mapPoint(noseLeft);

  ctx.moveTo(noseTopPoint.x, noseTopPoint.y);
  ctx.lineTo(noseRightPoint.x, noseRightPoint.y);
  ctx.lineTo(noseLeftPoint.x, noseLeftPoint.y);

  ctx.closePath();
  ctx.fill();
}

/**
 * Apple zone 마스크 그리기 (사과 존 - 원형)
 */
function drawAppleZoneMask(ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, mapPoint: LandmarkMapper) {
  // 왼쪽 애플 존 기준 포인트
  const leftTop = landmarks[50];
  const leftBottom = landmarks[36];

  // 오른쪽 애플 존 기준 포인트
  const rightTop = landmarks[280];
  const rightBottom = landmarks[266];

  const drawCircle = (top: any, bottom: any) => {
    const topPoint = mapPoint(top);
    const bottomPoint = mapPoint(bottom);
    const centerX = (topPoint.x + bottomPoint.x) / 2;
    const centerY = (topPoint.y + bottomPoint.y) / 2;

    const radius = Math.hypot(topPoint.x - bottomPoint.x, topPoint.y - bottomPoint.y) * 0.6; // 크기 조절 계수

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

      // 캔버스 크기를 실제 렌더 크기와 동기화 (HiDPI 포함)
      const rect = canvas.getBoundingClientRect();
      const dpr = window.devicePixelRatio || 1;
      const targetWidth = Math.max(1, Math.round(rect.width * dpr));
      const targetHeight = Math.max(1, Math.round(rect.height * dpr));

      if (canvas.width !== targetWidth || canvas.height !== targetHeight) {
        canvas.width = targetWidth;
        canvas.height = targetHeight;
      }

      // CSS 픽셀 기준으로 그리기
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);

      // 이전 프레임 지우기
      ctx.clearRect(0, 0, rect.width, rect.height);

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

      // object-fit: cover 기준으로 좌표 보정
      const scale = Math.max(rect.width / videoWidth, rect.height / videoHeight);
      const scaledWidth = videoWidth * scale;
      const scaledHeight = videoHeight * scale;
      const offsetX = (rect.width - scaledWidth) / 2;
      const offsetY = (rect.height - scaledHeight) / 2;
      const mapPoint: LandmarkMapper = (landmark) => ({
        x: landmark.x * scaledWidth + offsetX,
        y: landmark.y * scaledHeight + offsetY,
      });

      // 마스크 색상 설정
      ctx.fillStyle = config.color;

      // 마스크 그리기
      config.drawMask(ctx, landmarks, mapPoint);
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
      processFrame();
    };

    if (videoElement.readyState >= 2) {
      startProcessing();
    } else {
      videoElement.addEventListener("loadeddata", startProcessing);
    }

    // 클린업
    return () => {
      isCancelled = true;

      if (animationFrameRef.current) {
        cancelAnimationFrame(animationFrameRef.current);
        animationFrameRef.current = null;
      }

      if (faceMeshRef.current) {
        try {
          faceMeshRef.current.close();
        } catch (error) {
          console.warn("FaceMesh 종료 중 오류:", error);
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

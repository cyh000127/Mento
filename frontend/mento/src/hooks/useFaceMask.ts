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
  // 코 브릿지
  const noseBridgeTop = landmarks[6];

  // 코 측면
  const noseLeftTop = landmarks[129];
  const noseRightTop = landmarks[358];

  // 코 날개
  const noseLeftWing = landmarks[219];
  const noseRightWing = landmarks[439];

  // 코 끝
  const noseTip = landmarks[4];
  const noseBottom = landmarks[19];

  ctx.beginPath();

  ctx.moveTo(noseBridgeTop.x * canvas.width, noseBridgeTop.y * canvas.height);
  ctx.lineTo(noseRightTop.x * canvas.width, noseRightTop.y * canvas.height);
  ctx.lineTo(noseRightWing.x * canvas.width, noseRightWing.y * canvas.height);
  ctx.lineTo(noseBottom.x * canvas.width, noseBottom.y * canvas.height);
  ctx.lineTo(noseTip.x * canvas.width, noseTip.y * canvas.height);
  ctx.lineTo(noseLeftWing.x * canvas.width, noseLeftWing.y * canvas.height);
  ctx.lineTo(noseLeftTop.x * canvas.width, noseLeftTop.y * canvas.height);

  ctx.closePath();
  ctx.fill();
}

/**
 * Apple zone 마스크 그리기 (사과 존 - 볼 상단)
 */
function drawAppleZoneMask(ctx: CanvasRenderingContext2D, landmarks: NormalizedLandmarkList, canvas: HTMLCanvasElement) {
  // 왼쪽 사과 존
  const leftAppleTop = landmarks[50];
  const leftAppleOuter = landmarks[117];
  const leftAppleBottom = landmarks[36];
  const leftAppleInner = landmarks[205];

  // 오른쪽 사과 존
  const rightAppleTop = landmarks[280];
  const rightAppleOuter = landmarks[346];
  const rightAppleBottom = landmarks[266];
  const rightAppleInner = landmarks[425];

  // 왼쪽 사과 존
  ctx.beginPath();
  ctx.moveTo(leftAppleTop.x * canvas.width, leftAppleTop.y * canvas.height);
  ctx.lineTo(leftAppleOuter.x * canvas.width, leftAppleOuter.y * canvas.height);
  ctx.lineTo(leftAppleBottom.x * canvas.width, leftAppleBottom.y * canvas.height);
  ctx.lineTo(leftAppleInner.x * canvas.width, leftAppleInner.y * canvas.height);
  ctx.closePath();
  ctx.fill();

  // 오른쪽 사과 존
  ctx.beginPath();
  ctx.moveTo(rightAppleTop.x * canvas.width, rightAppleTop.y * canvas.height);
  ctx.lineTo(rightAppleOuter.x * canvas.width, rightAppleOuter.y * canvas.height);
  ctx.lineTo(rightAppleBottom.x * canvas.width, rightAppleBottom.y * canvas.height);
  ctx.lineTo(rightAppleInner.x * canvas.width, rightAppleInner.y * canvas.height);
  ctx.closePath();
  ctx.fill();
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
    color: "rgba(255, 100, 150, 0.35)", // 핑크색
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

  useEffect(() => {
    // 마스크가 선택되지 않았거나 필수 요소가 없으면 종료
    if (!videoElement || !canvasRef.current || !maskType) {
      return;
    }

    const canvas = canvasRef.current;
    const ctx = canvas.getContext("2d");

    if (!ctx) {
      console.error("Canvas 2D context를 가져올 수 없습니다.");
      return;
    }

    console.log(`🎭 FaceMesh 초기화 시작 - ${maskType}`);

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

      // 캔버스 크기를 비디오 크기와 동기화
      if (canvas.width !== videoElement.videoWidth || canvas.height !== videoElement.videoHeight) {
        canvas.width = videoElement.videoWidth;
        canvas.height = videoElement.videoHeight;
      }

      // 이전 프레임 지우기
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      // 얼굴이 감지되지 않으면 리턴
      if (!results.multiFaceLandmarks || results.multiFaceLandmarks.length === 0) {
        return;
      }

      const landmarks = results.multiFaceLandmarks[0];
      const config = MASK_CONFIGS[maskType];

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
    const processFrame = async () => {
      if (!videoElement || videoElement.readyState < 2) {
        animationFrameRef.current = requestAnimationFrame(processFrame);
        return;
      }

      try {
        await faceMesh.send({ image: videoElement });
      } catch (error) {
        console.error("FaceMesh 처리 중 오류:", error);
      }

      animationFrameRef.current = requestAnimationFrame(processFrame);
    };

    const startProcessing = () => {
      console.log(`✅ FaceMesh 처리 시작 - ${maskType}`, {
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
      console.log(`🧹 FaceMesh 클린업 - ${maskType}`);

      if (animationFrameRef.current) {
        cancelAnimationFrame(animationFrameRef.current);
        animationFrameRef.current = null;
      }

      if (faceMeshRef.current) {
        faceMeshRef.current.close();
        faceMeshRef.current = null;
      }

      videoElement.removeEventListener("loadeddata", startProcessing);

      // 캔버스 초기화
      if (ctx) {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
      }
    };
  }, [videoElement, canvasRef, maskType]);

  return faceMeshRef;
}

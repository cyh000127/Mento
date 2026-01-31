import { useEffect, useRef } from "react";
import { FaceMesh, Results } from "@mediapipe/face_mesh";

/**
 * MediaPipe FaceMesh를 사용하여 T-zone 마스크 오버레이를 그리는 커스텀 훅
 * 수정된 버전: 알파벳 'T' 모양에 최적화된 랜드마크 적용
 */
export function useTZoneFaceMask(videoElement: HTMLVideoElement | null, canvasRef: React.RefObject<HTMLCanvasElement | null>, enabled: boolean = true) {
  const faceMeshRef = useRef<FaceMesh | null>(null);
  const animationFrameRef = useRef<number | null>(null);

  useEffect(() => {
    if (!videoElement || !canvasRef.current || !enabled) {
      return;
    }

    const canvas = canvasRef.current;
    const ctx = canvas.getContext("2d");

    if (!ctx) {
      console.error("Canvas 2D context를 가져올 수 없습니다.");
      return;
    }

    console.log("🎭 T-zone FaceMesh 초기화 시작");

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

    faceMesh.onResults((results: Results) => {
      if (!canvas || !ctx) return;

      if (canvas.width !== videoElement.videoWidth || canvas.height !== videoElement.videoHeight) {
        canvas.width = videoElement.videoWidth;
        canvas.height = videoElement.videoHeight;
      }

      ctx.clearRect(0, 0, canvas.width, canvas.height);

      if (!results.multiFaceLandmarks || results.multiFaceLandmarks.length === 0) {
        return;
      }

      const landmarks = results.multiFaceLandmarks[0];

      // ==========================================================
      // [수정됨] 알파벳 'T' 모양을 만들기 위한 랜드마크 재정의
      // ==========================================================

      // 1. 이마 가로바 (Top Bar)
      const foreheadTop = landmarks[10]; // 이마 중앙 상단
      const foreheadLeft = landmarks[103]; // 이마 왼쪽 끝 (가로바 시작)
      const foreheadRight = landmarks[332]; // 이마 오른쪽 끝 (가로바 끝)

      // 2. 가로바 하단 경계 (Temple area)
      const templeLeft = landmarks[66]; // 왼쪽 관자놀이 부근 (가로바 좌측 하단)
      const templeRight = landmarks[296]; // 오른쪽 관자놀이 부근 (가로바 우측 하단)

      // 3. T의 꺾이는 부분 (Inner Brow - 눈썹 안쪽)
      // 이 부분이 T의 가로와 세로가 만나는 겨드랑이 부분입니다.
      const browInnerLeft = landmarks[107]; // 왼쪽 눈썹 안쪽
      const browInnerRight = landmarks[336]; // 오른쪽 눈썹 안쪽

      // 4. 콧대 (Vertical Stem)
      // 콧볼(Wing)이 아닌 콧대(Bridge) 라인을 사용하여 일자로 떨어지게 함
      const noseBridgeLeft = landmarks[193]; // 콧대 왼쪽 라인
      const noseBridgeRight = landmarks[417]; // 콧대 오른쪽 라인
      const noseTip = landmarks[4]; // 코 끝 (T의 최하단)

      // ==========================================================
      // 그리기 로직
      // ==========================================================
      ctx.fillStyle = "rgba(255, 200, 0, 0.35)";
      ctx.beginPath();

      // 시계 방향으로 T 모양 그리기

      // 1. 왼쪽 상단에서 시작 -> 오른쪽으로 이동 (이마 윗변)
      ctx.moveTo(foreheadLeft.x * canvas.width, foreheadLeft.y * canvas.height);
      ctx.lineTo(foreheadTop.x * canvas.width, foreheadTop.y * canvas.height); // 곡선을 위해 중앙 경유
      ctx.lineTo(foreheadRight.x * canvas.width, foreheadRight.y * canvas.height);

      // 2. 오른쪽 끝에서 아래로 -> 안쪽으로 꺾기 (T의 오른쪽 날개)
      ctx.lineTo(templeRight.x * canvas.width, templeRight.y * canvas.height);
      ctx.lineTo(browInnerRight.x * canvas.width, browInnerRight.y * canvas.height); // 여기서 안쪽으로 확 꺾임

      // 3. 콧대 타고 내려가기 (T의 기둥 오른쪽)
      ctx.lineTo(noseBridgeRight.x * canvas.width, noseBridgeRight.y * canvas.height);

      // 4. 코 끝 (T의 바닥)
      ctx.lineTo(noseTip.x * canvas.width, noseTip.y * canvas.height);

      // 5. 콧대 타고 올라오기 (T의 기둥 왼쪽)
      ctx.lineTo(noseBridgeLeft.x * canvas.width, noseBridgeLeft.y * canvas.height);

      // 6. 왼쪽 안쪽에서 바깥으로 꺾기 (T의 왼쪽 날개 시작)
      ctx.lineTo(browInnerLeft.x * canvas.width, browInnerLeft.y * canvas.height); // 여기서 바깥으로 확 꺾임
      ctx.lineTo(templeLeft.x * canvas.width, templeLeft.y * canvas.height);

      // 7. 시작점으로 닫기
      ctx.closePath();
      ctx.fill();
    });

    faceMeshRef.current = faceMesh;

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
      processFrame();
    };

    if (videoElement.readyState >= 2) {
      startProcessing();
    } else {
      videoElement.addEventListener("loadeddata", startProcessing);
    }

    return () => {
      if (animationFrameRef.current) {
        cancelAnimationFrame(animationFrameRef.current);
      }
      if (faceMeshRef.current) {
        faceMeshRef.current.close();
      }
      videoElement.removeEventListener("loadeddata", startProcessing);
      if (ctx) {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
      }
    };
  }, [videoElement, canvasRef, enabled]);

  return faceMeshRef;
}

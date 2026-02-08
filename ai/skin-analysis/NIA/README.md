## Reference
 NIA https://github.com/leejeongho3214/NIA


# AI Skin Analysis System

## 프로젝트 개요

딥러닝을 활용한 피부 분석 AI 시스템입니다. 사용자의 얼굴 이미지를 분석하여 5가지 피부 항목(수분, 모공, 주름, 색소, 탄력)에 대한 점수와 상태를 진단합니다.

- **주요 기능**:
    *   **다중 속성 분석**: 수분(Moisture), 모공(Pore), 주름(Wrinkle), 색소(Pigmentation), 탄력(Sagging)
    *   **자동 얼굴 인식**: MediaPipe 기반 얼굴 랜드마크 감지 및 정밀 크롭
    *   **고속 추론**: ONNX Runtime 최적화를 통한 CPU 환경에서의 실시간 추론 (~108ms)
    *   **Docker 지원**: 컨테이너 기반 배포 환경 제공

---

## 1. 리포지토리 구조

```
.
├── app/                # 서비스 애플리케이션 (FastAPI/Inference)
│   ├── main.py         # 진입점
│   ├── analysis/       # 추론 로직 (service_inference.py)
│   └── resources/      # 모델 가중치 및 설정 파일
├── core/               # 모델 학습 코어
│   ├── main.py         # 학습 스크립트
│   └── model/          # 모델 정의 (EfficientNet-b0 등)
├── scripts/            # 유틸리티 스크립트
│   ├── preprocess_dataset_mediapipe.py # 데이터 전처리
│   └── convert_to_onnx.py              # ONNX 변환
├── docs/               # 개발 문서 및 리포트
└── dataset/            # 학습 데이터 (별도 관리)
```

---

## 2. 설치 및 실행 가이드

### 2.1 사전 요구사항
*   **Python 3.12**
*   **uv** (Python 패키지 매니저)

### 2.2 설치
```bash
# 의존성 설치
uv sync
```

### 2.3 로컬 실행
```bash
# 추론 서버 실행 (Port 4000)
uv run app/main.py
```

### 2.4 Docker 실행
```bash
# 이미지 빌드
docker build -t skin-analysis .

# 컨테이너 실행
docker run -p 4000:4000 skin-analysis
```

---

## 3. 학습 파이프라인 (Training Pipeline)

새로운 데이터로 모델을 학습하려면 다음 단계를 순서대로 진행합니다.

### 3.1 데이터 전처리
MediaPipe를 사용하여 원본 이미지를 학습용 크롭 이미지로 변환합니다.
```bash
# 데이터셋: dataset/img -> dataset/crop
uv run scripts/preprocess_dataset_mediapipe.py
```

### 3.2 모델 학습
회귀(Regression) 모델과 분류(Classification) 모델을 각각 학습합니다.

**회귀 모델 (Moisture, Pore, Pigmentation, Wrinkle_Eye):**
```bash
uv run python core/main.py --mode regression --name 1st_robust --equ 1 2 3 --epoch 100
```

**분류 모델 (Sagging, Wrinkle_Forehead/Glabellus):**
```bash
uv run python core/main.py --mode class --name 1st_robust --equ 1 2 3 --epoch 100
```

### 3.3 ONNX 변환 및 배포
학습된 PyTorch 모델(`.pt`)을 최적화된 ONNX 모델(`.onnx`)로 변환하여 `app/resources/weights`로 복사합니다.
```bash
uv run scripts/convert_to_onnx.py
```

---

## 4. 성능 리포트

### 4.1 추론 속도 (CPU 기준)
| 엔진 | 평균 지연 시간 | 비고 |
|:---|:---:|:---|
| **ONNX Runtime** | **~108 ms** | PyTorch 대비 약 30ms 느리지만 안정적 |
| PyTorch | ~78 ms | |

### 4.2 경량화 결과
*   **모델 파일 크기 (Checkpoints)**:
    -   PyTorch (`.bin`): **~834 MB**
    -   ONNX (`.onnx`): **~282 MB** (약 66% 감소)
*   **Docker 이미지 크기**: ~1.7GB (PyTorch) → **~300MB (ONNX)** (약 82% 감소)
*   **메모리 최적화**: `enable_cpu_mem_arena=False` 설정으로 메모리 스파이크 방지

---

## 5. 문제 해결 (Troubleshooting)

### 5.1 점수가 100점(최악)으로 나오는 경우
*   **증상**: 수분 등의 항목 점수가 100점이 나오며, `raw_value`가 `0.0`입니다.
*   **원인**: 얼굴 인식 실패. 특히 **측면(L30, R30) 촬영 각도**가 잘못된 경우 발생합니다.
*   **해결책**:
    - **L30 (Left 30°)**: 왼쪽 뺨이 보여야 하므로, 고개를 **오른쪽**으로 돌려 촬영합니다.
    - **R30 (Right 30°)**: 오른쪽 뺨이 보여야 하므로, 고개를 **왼쪽**으로 돌려 촬영합니다.

### 5.2 메모리 사용량 급증
*   **증상**: 서버 실행 중 메모리가 3GB 이상으로 치솟음.
*   **해결**: `service_inference.py`에서 `sess_options.enable_cpu_mem_arena = False` 설정 확인.


 
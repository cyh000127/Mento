# 파이프라인 진단 및 개선 기록

## 개요

본 프로젝트의 추론 정확도가 기대에 미치지 못해 원인 분석을 진행했습니다. 코드 리뷰와 디버깅 스크립트(`debug_crop_alignment.py`) 실행 결과, 학습과 추론 간의 크롭 영역 불일치가 핵심 원인으로 확인되었습니다.

---

## 1. 발견된 문제점

### 1.1 크롭 영역 불일치

학습 시에는 `dataset/label/`의 수동 바운딩 박스를 사용했으나, 추론 시에는 MediaPipe 랜드마크 기반으로 크롭하고 있었습니다.

직접 비교해본 결과:
- 추론 크롭이 학습 크롭 대비 좌측 약 100px, 하단 약 40px 이동
- 추론 크롭이 약 15% 더 넓음 (Zoom-out)

결국 모델이 학습한 영역과 다른 곳을 보고 있었습니다.

### 1.2 학습 코드 이슈

`core/main.py` 분석 중 발견한 문제들:

- Early Stopping 카운터가 에폭마다 리셋되지 않아 제대로 동작하지 않음
- `elasticity_R2`, `dryness` 등 서비스에서 사용하지 않는 특성까지 학습에 포함되어 있었음
- 최적화 기법(`AdamW`, `ReduceLROnPlateau`)이 미적용 상태였음

### 1.3 Python 버전 이슈

Python 3.9 → 3.12 마이그레이션 중 `np.Inf` 등 deprecated API 사용으로 인한 오류 발생 (수정 완료)

---

## 2. 적용한 수정 사항

### 2.1 학습 대상 정리

서비스에서 실제 사용하는 모델만 학습하도록 `core/main.py` 수정:

- 회귀: `moisture`, `pore`, `pigmentation`, `wrinkle_Ra`
- 분류: `sagging`, `wrinkle`

### 2.2 캘리브레이션 룰 재계산

`experiments/research/calibration.py`를 실행해서 MediaPipe 랜드마크 기반 최적 크롭 파라미터를 `calibration_rules.json`에 저장했습니다.

### 2.3 데이터셋 재생성

`scripts/preprocess_dataset_mediapipe.py`로 MediaPipe 기반 크롭을 적용한 새 데이터셋을 생성했습니다. 학습과 추론에서 동일한 크롭 로직을 사용하게 됩니다.

---

## 3. 재학습 명령어

```bash
# 회귀 모델
uv run python core/main.py --mode regression --name 1st_robust --equ 1 2 3 --epoch 100

# 분류 모델
uv run python core/main.py --mode class --name 1st_robust --equ 1 2 3 --epoch 100

# ONNX 변환
uv run python scripts/convert_to_onnx.py
```

---

## 4. 결과

재학습 후 추론 테스트에서 기존 대비 예측값이 정상 범위로 출력되는 것을 확인했습니다. 학습-추론 도메인 일치가 핵심이었습니다.

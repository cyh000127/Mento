# 피부 분석 모델 성능 비교 및 추천 리포트

## 1. 개요
본 문서는 피부 분석을 위한 AI 모델의 성능을 비교 분석하고, 최적의 모델 운영 전략을 제안합니다.
*   **비교 대상 모델**: `1st_cnn` vs `1st_coat`
*   **비교 방식**: Classification vs Regression (회귀)
*   **분석 데이터**: 2026-01-26 평가 로그 기준

## 2. 모델 아키텍처 비교 (1st_cnn vs 1st_coat)
Classification 모델 간의 성능을 비교한 결과, **`1st_coat` 모델이 압도적으로 우수**했습니다.

| 모델 | 성능 요약 | 평가 |
| :--- | :--- | :--- |
| **1st_cnn** | 대부분의 항목에서 Correlation 0에 근접, 높은 MAE | **사용 부적합** (학습 실패 또는 성능 미달) |
| **1st_coat** | 주요 항목에서 높은 Correlation(0.6 ~ 0.8) 및 낮은 MAE | **사용 적합** |

> [!IMPORTANT]
> `1st_cnn` 모델은 사실상 유의미한 예측을 하지 못하고 있으므로, 배포 및 추가 실험에서 제외할 것을 권장합니다.

## 3. 방법론 비교 (Classification vs Regression)
`1st_coat` 모델을 기준으로, Classification(등급 분류)과 Regression(수치 예측) 방식의 성능을 항목별로 비교했습니다.

### 3.1. 성능 비교표 (Correlation 기준)

| 항목 (Feature) | Classification (Correlation) | Regression (Correlation) | 승자 | 비고 |
| :--- | :--- | :--- | :--- | :--- |
| **Pigmentation** (색소) | ~0.70 | **~0.95** (L30/R30) | **Regression** | Regression이 매우 정밀함 |
| **Pore** (모공) | ~0.50 | **~0.86** (L30/R30) | **Regression** | Class 모델은 성능이 저조함 |
| **Moisture** (수분) | ~0.29 (Dryness) | **~0.65** (Cheek) | **Regression** | Class 모델은 등급 구분 어려움 |
| **Sagging** (처짐/탄력) | **~0.80** | ~0.69 (Elasticity) | **Classification** | Classification이 더 안정적임 |
| **Wrinkle** (주름) | **~0.80** (전 부위) | N/A (일부만 존재) | **Classification** | Regression은 눈가 주름만 존재 |

### 3.2. 상세 분석
*   **Regression 우세 항목**: 연속적인 수치 변화가 중요한 항목들(색소 밀도, 모공 개수/크기, 수분량)에서 Regression 모델이 월등한 성능을 보였습니다. 특히 색소와 모공은 Classification으로 억지스런 등급을 매기는 것보다 수치 예측이 훨씬 정확했습니다.
*   **Classification 우세 항목**: 처짐(Sagging)이나 주름(Wrinkle)은 육안 기준의 임상 등급(Grade 0~6)과 매칭되는 경향이 강해 Classification 모델이 더 높은 상관관계를 보였습니다.

## 4. 최종 추천 전략
부위 및 분석 항목의 특성에 따라 모델 타입을 이원화하여 운영하는 **하이브리드 전략**을 제안합니다.

| 분석 항목 | 추천 모델 타입 | 사유 |
| :--- | :--- | :--- |
| **모공 (Pore)** | **Regression** | 높은 상관관계(0.86)로 정밀한 상태 진단 가능 |
| **색소 (Pigmentation)** | **Regression** | 현존 최고 성능(0.95+), 미세한 변화 감지 탁월 |
| **수분 (Moisture/Dryness)** | **Regression** | 건조도는 등급 분류보다 수치화된 예측이 훨씬 유리함 |
| **탄력/처짐 (Sagging)** | **Classification** | 임상 등급 기준의 분류가 Elasticity 수치 예측보다 정확함 |
| **주름 (Wrinkle)** | **Classification** | 이마, 미간, 눈가 등 모든 부위에서 Class 모델 성능이 안정적임 |

## 5. 향후 계획 (Next Steps)
1.  **모델 파이프라인 분리**: Inference 시 항목별로 다른 모델(Class/Reg)을 호출하도록 로직 변경 필요
2.  **데이터 매핑**: Regression 출력값(실수)을 사용자 친화적인 점수나 등급(UI 표시용)으로 변환하는 후처리 로직 개발 필요

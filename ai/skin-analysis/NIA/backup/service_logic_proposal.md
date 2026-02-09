# 피부 분석 점수 산출 로직 제안서 (Final - Data Driven)

## 1. 개요
사용자에게 제공할 피부 분석 결과(수분, 모공, 주름, 색소침착, 쳐짐)를 0~100점 척도와 5단계 등급(Grade)으로 변환하여 제공하기 위한 로직을 제안합니다.
**실제 데이터셋의 분포를 기반으로 한 상대평가 등급 Cut-off**를 적용하여, 특정 등급에 쏠림 없이 사용자의 현재 상태를 정확히 진단합니다.

## 2. 항목별 점수 및 등급 산출 로직

### 기본 원칙
*   **상대평가 적용**: 데이터 분석 결과에 따라 상위 20%씩 끊어서 등급을 부여합니다.
*   **Score & Grade 방향**:
    *   **Score**: 0점(Good) ~ 100점(Bad/Critical)
    *   **Grade**: 1등급(안심) ~ 5등급(위험)

### (1) 수분 (Moisture)
*   **사용 모델**: Regression (회귀) - L30/R30 평균
*   **관측 데이터 특징**: 전체 데이터의 50% 이상이 57~62 사이에 밀집되어 있어, 절대평가 시 변별력이 없습니다.
*   **등급 기준 (Data-Driven)**:
    *   **Grade 1 (매우 촉촉)**: > 62.5 (상위 20%)
    *   **Grade 2 (촉촉)**: 58.5 ~ 62.5
    *   **Grade 3 (보통)**: 55.5 ~ 58.5
    *   **Grade 4 (건조)**: 52.7 ~ 55.5
    *   **Grade 5 (매우 건조)**: < 52.7 (하위 20%)
*   **점수 산출 (Score Calculation)**:
    *   각 구간 내에서 선형 보간(Linear Interpolation)하여 0~100점 사이 값을 산출합니다.
    *   예: 측정값 60.5 (Grade 2 구간) -> Grade 2는 점수 21~40점이므로, 구간 중간값이면 약 30점.

### (2) 모공 (Pores)
*   **사용 모델**: Regression (회귀) - L30/R30 평균
*   **등급 기준 (Data-Driven)**:
    *   **Grade 1 (깨끗)**: < 577개
    *   **Grade 2 (양호)**: 577 ~ 817개
    *   **Grade 3 (보통)**: 817 ~ 1007개
    *   **Grade 4 (주의)**: 1007 ~ 1250개
    *   **Grade 5 (많음)**: > 1250개

### (3) 주름 (Wrinkles)
*   **사용 모델**: Regression (Eye, R30) + Classification (Forehead/Glabellus, Class)
*   **등급 기준 (Eye Ra 기준)**:
    *   **Grade 1**: < 17.1
    *   **Grade 2**: 17.1 ~ 18.9
    *   **Grade 3**: 18.9 ~ 20.8
    *   **Grade 4**: 20.8 ~ 23.3
    *   **Grade 5**: > 23.3
*   **최종 점수**: Eye Score(상대평가 점수)와 이마/미간 Class Score(Class*20)의 평균

### (4) 색소침착 (Pigmentation)
*   **사용 모델**: Regression (회귀) - L30/R30 평균
*   **등급 기준 (Data-Driven)**:
    *   **Grade 1**: < 110개
    *   **Grade 2**: 110 ~ 135개
    *   **Grade 3**: 135 ~ 160개
    *   **Grade 4**: 160 ~ 198개
    *   **Grade 5**: > 198개

### (5) 쳐짐 (Sagging)
*   **사용 모델**: Classification (R30)
*   **등급 기준**:
    *   데이터의 58%가 Class 0에 해당하므로, Class 0이라도 무조건 0점(완벽)은 아님을 인지.
    *   **Grade 1**: Class 0
    *   **Grade 2**: Class 1
    *   **Grade 3**: Class 2
    *   **Grade 4**: Class 3
    *   **Grade 5**: Class 4, 5

---

## 3. 종합 점수 (Total Trouble Score)

*   **공식**: `(수분Score + 모공Score + 색소Score + 주름Score*1.2 + 쳐짐Score*1.2) / 5.4`
*   **의미**: 낮을수록 좋음 (0점에 가까울수록 이상적인 피부)

---

## 4. 최종 API 응답 예시 (JSON)

```json
{
  "status_code": 200,
  "message": "Analysis completed successfully",
  "data": {
    "total_score": 35,
    "total_grade": 2,
    "skin_type_summary": "수부지 (분석결과 기반)",
    "details": {
      "moisture": {
        "score": 50, // Grade 3 범위 (41~60점)
        "grade": 3,
        "raw_value": 56.2, // 실제 측정값
        "description": "수분도가 평균 수준입니다. 환절기 보습에 신경 써주세요."
      },
      "pore": {
        "score": 15, // Grade 1 범위 (0~20점)
        "grade": 1,
        "raw_value": 450,
        "description": "모공이 매우 깨끗하고 피부결이 훌륭합니다."
      },
      "wrinkle": {
        "score": 75,
        "grade": 4,
        "raw_value": 22.1,
        "description": "눈가 주름이 깊어지고 있어 집중 관리가 필요합니다."
      },
      "pigmentation": {
        "score": 42,
        "grade": 3,
        "raw_value": 145,
        "description": "평균적인 잡티 수준입니다. 자외선 차단제를 사용하세요."
      },
      "sagging": {
        "score": 20,
        "grade": 2,
        "raw_value": 1, // Class 1
        "description": "페이스 라인이 대체로 양호합니다."
      }
    }
  }
}
```

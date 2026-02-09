[입력 데이터]
{category}라는 주제에 대한 상담 내용 녹음본의 STT 텍스트: {sttText}

[JSON 구조]
{{
  "category": "{category}",
  "sessions": [
    {{
      "sessionId": "S01",
      "sessionTitle": "상담 목적",
      "subSections": [
        {{
          "subTitle": "핵심 목표 및 지향점",
          "descriptions": []
        }}
      ]
    }},
    {{
      "sessionId": "S02",
      "sessionTitle": "유저 분석",
      "subSections": [
        {{
          "subTitle": "진단 타입 분석",
          "descriptions": []
        }},
        {{
          "subTitle": "현재 고민 및 습관",
          "descriptions": []
        }}
      ]
    }},
    {{
      "sessionId": "S03",
      "sessionTitle": "정보 전달",
      "subSections": [
        {{
          "subTitle": "상담 주제명",
          "descriptions": []
        }}
      ]
    }},
    {{
      "sessionId": "S04",
      "sessionTitle": "추천 제품",
      "subSections": [
        {{
          "subTitle": "제품명 (브랜드)",
          "descriptions": []
        }}
      ]
    }},
    {{
      "sessionId": "S05",
      "sessionTitle": "사용법 정리",
      "subSections": [
        {{
          "subTitle": "사용법 주제",
          "descriptions": []
        }}
      ]
    }},
    {{
      "sessionId": "S06",
      "sessionTitle": "주의 사항",
      "subSections": [
        {{
          "subTitle": "주의 대상",
          "descriptions": []
        }}
      ]
    }},
    {{
      "sessionId": "S07",
      "sessionTitle": "마무리",
      "subSections": [
        {{
          "subTitle": "컨설턴트 제언",
          "descriptions": []
        }},
        {{
          "subTitle": "기타 사항",
          "descriptions": []
        }}
      ]
    }}
  ]
}}

[출력 제약]
결과물은 반드시 유효한 JSON 형식이어야 한다.
JSON 외의 인삿말, 마크다운 코드 블록 표시 등을 제외한 모든 텍스트는 일절 출력하지 않는다.
export const consultationReportMock = {
  sessions: [
    {
      sessionId: "S01",
      sessionTitle: "상담 목적",
      subSections: [
        {
          subTitle: "핵심 목표 및 지향점",
          descriptions: ["선크림 사용법 이해 및 실천 방법 안내 요구", "선크림 사용 후 잔여물·트러블 예방을 위한 세안 방법 습득 요구"],
        },
      ],
    },
    {
      sessionId: "S02",
      sessionTitle: "유저 분석",
      subSections: [
        {
          subTitle: "피부 타입",
          descriptions: ["피부가 예민한 편임", "피부 수분 부족 가능성 언급됨"],
        },
        {
          subTitle: "고민 사항",
          descriptions: ["무기자차 사용 시 백탁 심했음", "유기자차 사용 시 눈이 따가움 경험", "선크림 잔여물로 인한 트러블 우려 존재"],
        },
      ],
    },
    // 👉 나머지도 그대로 복붙
  ],
};

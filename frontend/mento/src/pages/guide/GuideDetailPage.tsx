import { useParams, useNavigate } from "react-router-dom"
import { useState, useEffect } from "react"
import { FaceCameraSection } from "../../components/guide/FaceCameraSection"
import { StepGuide } from "../../components/guide/StepGuide"
import { ArrowLeft } from "lucide-react"
import { PRODUCT_SUBTYPES } from "@/constants/guide"

const usageSteps: Record<string, Record<string, { steps: any[], tip: string }>> = {
  // 스킨케어 - 클렌징
  "클렌징": {
    "foam": {
      "steps": [
        { "number": 1, "title": "거품 생성", "description": "손바닥에 검지 한 마디 정도를 덜어 물과 함께 풍성한 거품을 만듭니다." },
        { "number": 2, "title": "T존 집중 세안", "description": "기름기가 많은 T존을 중심으로 거품을 올려 원을 그리며 꼼꼼히 문지릅니다." },
        { "number": 3, "title": "U존 정돈", "description": "면도로 예민해진 U존과 사과존은 남은 거품으로 살살 굴리듯 가볍게 닦아냅니다." },
        { "number": 4, "title": "미온수 헹구기", "description": "미온수로 미끈거림이 없을 때까지 15회 이상 충분히 어푸어푸 헹굽니다." }
      ],
      "tip": "너무 과한 세안은 피부를 자극할 수 있으니, 하루에 3번 이상은 사용을 권장하지 않습니다."
    },
    "oil": {
      "steps": [
        { "number": 1, "title": "오일 도포", "description": "물기 없는 손에 500원 동전 크기로 펌핑하여 얼굴 전체에 부드럽게 펴 바릅니다." },
        { "number": 2, "title": "블랙헤드 녹이기", "description": "피지가 쌓여 딱딱해지기 쉬운 코 부위를 30초 동안 집중적으로 롤링합니다." },
        { "number": 3, "title": "유화 과정", "description": "손에 물을 약간 묻혀 오일이 우윳빛으로 변하면 T존부터 U존까지 한 번 더 마사지합니다." },
        { "number": 4, "title": "물 세안", "description": "오일 잔여물이 모공에 남지 않도록 미온수로 깨끗이 씻어냅니다." }
      ],
      "tip": "기름을 기름으로 녹여내는 원리라 블랙헤드 제거에 좋지만, 1분 이상 문지르면 오히려 노폐물이 다시 모공에 들어갑니다."
    },
    "water": {
      "steps": [
        { "number": 1, "title": "화장솜 적시기", "description": "화장솜이 흠뻑 젖어 투명해질 때까지 500원 동전보다 크게 워터를 적십니다." },
        { "number": 2, "title": "결 닦아내기", "description": "사과존과 U존을 안쪽에서 바깥쪽으로 피부 결을 따라 가볍게 쓸어내듯 닦습니다." },
        { "number": 3, "title": "포인트 클렌징", "description": "피지가 많은 T존과 굴곡진 코 부위는 워터를 적신 화장솜을 5초간 눌러 녹여냅니다." },
        { "number": 4, "title": "가벼운 헹굼", "description": "물 세안이 필수는 아니지만, 미온수로 가볍게 어푸어푸 세안하여 마무리합니다." }
      ],
      "tip": "귀찮아서 대충 닦으면 화장솜 마찰 때문에 피부가 따가워지니, 무조건 솜을 축축하게 적셔야 합니다."
    },
    "balm": {
      "steps": [
        { "number": 1, "title": "밤 녹이기", "description": "스패출러로 엄지손톱만큼 덜어 손바닥 열기로 살짝 녹인 뒤 얼굴에 도포합니다." },
        { "number": 2, "title": "노폐물 제거", "description": "넓은 사과존부터 시작해 U존까지 전체적으로 마사지하며 베이스 제품을 녹입니다." },
        { "number": 3, "title": "블랙헤드 공략", "description": "유분이 많은 T존과 블랙헤드가 박힌 코 부위를 손가락 끝으로 세밀하게 문지릅니다." },
        { "number": 4, "title": "유화 후 세안", "description": "손에 물을 묻혀 우윳빛으로 유화시킨 뒤 잔여감 없이 미온수로 헹굽니다." }
      ],
      "tip": "오일보다 세정력이 강력해서 선크림이나 메이크업을 빡세게 한 날에만 쓰는 걸 추천합니다."
    },
    "pad": {
      "steps": [
        { "number": 1, "title": "엠보싱 케어", "description": "패드의 올록볼록한 면으로 피지 분비가 왕성한 T존과 코 부위를 먼저 닦습니다." },
        { "number": 2, "title": "피부결 정돈", "description": "패드 반대쪽 부드러운 면으로 사과존과 U존을 가볍게 쓸어 넘깁니다." },
        { "number": 3, "title": "각질 정리", "description": "각질이 일어나기 쉬운 턱 라인과 코 망울 옆을 한 번 더 세밀하게 닦습니다." },
        { "number": 4, "title": "마무리 세안", "description": "패드 액이 남지 않도록 미온수로 얼굴 전체를 가볍게 헹구어 냅니다." }
      ],
      "tip": "매일 쓰면 얼굴 가죽 다 벗겨지는 느낌 들 수 있으니, 술 먹고 세수하기 너무 귀찮은 비상시에만 쓰세요."
    },
    "gel": {
      "steps": [
        { "number": 1, "title": "젤 도포", "description": "손바닥에 500원 동전 크기로 덜어 물기가 있는 얼굴 전체에 부드럽게 펴 바릅니다." },
        { "number": 2, "title": "저자극 마사지", "description": "자극에 민감한 사과존을 중심으로 젤 제형을 천천히 굴려 노폐물을 녹여냅니다." },
        { "number": 3, "title": "피지 조절", "description": "유분이 잘 올라오는 T존과 코 부위를 손가락 끝을 이용해 원을 그리며 닦습니다." },
        { "number": 4, "title": "충분한 헹굼", "description": "젤 특유의 미끄러움이 사라질 때까지 시원하게 물 세안을 합니다." }
      ],
      "tip": "거품이 거의 안 나서 씻긴 건가 싶겠지만, 자고 일어난 아침에 개기름만 걷어내기 딱 좋습니다."
    }
  },
  // 스킨케어 - 토너·스킨
  "토너·스킨": {
    "water": {
      "steps": [
        { "number": 1, "title": "적당량 덜기", "description": "손바닥에 500원 동전 크기만큼 덜어낸 뒤 양손으로 가볍게 퍼뜨립니다." },
        { "number": 2, "title": "흡수시키기", "description": "자극에 민감한 사과존부터 시작해 얼굴 전체를 감싸듯 가볍게 두드려 흡수시킵니다." },
        { "number": 3, "title": "T존 정돈", "description": "번들거림이 쉬운 T존은 손가락 끝으로 톡톡 두드려 겉도는 내용물이 없게 합니다." },
        { "number": 4, "title": "진정 마무리", "description": "면도로 자극받은 U존을 손바닥 온기로 지긋이 눌러 수분을 밀착시킵니다." }
      ],
      "tip": "세안 후 물기가 마르기 전 1분 이내에 발라야 피부 속 수분을 뺏기지 않고 유지할 수 있습니다."
    },
    "essence": {
      "steps": [
        { "number": 1, "title": "내용물 도포", "description": "검지 한 마디 정도 양을 얼굴의 사과존과 이마에 나눠 찍어 바릅니다." },
        { "number": 2, "title": "결 따라 펴바르기", "description": "피부 결을 따라 안쪽에서 바깥쪽으로 부드럽게 밀어내듯 펴 바릅니다." },
        { "number": 3, "title": "U존 집중 케어", "description": "건조함이 심한 U존 부위는 한 번 더 덧발라 영양감을 충분히 전달합니다." },
        { "number": 4, "title": "두드려 밀착", "description": "얼굴 전체를 가볍게 태핑하며 잔여감이 남지 않도록 완전히 흡수시킵니다." }
      ],
      "tip": "에센스 타입은 고농축인 경우가 많으므로 한 번에 많이 바르기보다 얇게 겹쳐 바르는 것이 흡수에 효과적입니다."
    },
    "pad": {
      "steps": [
        { "number": 1, "title": "피부결 닦기", "description": "패드의 매끈한 면으로 사과존부터 바깥쪽을 향해 피부 결을 따라 부드럽게 닦아냅니다." },
        { "number": 2, "title": "T존 노폐물 제거", "description": "유분이 많은 T존과 굴곡진 코 부위를 패드로 가볍게 눌러 닦아내며 피부를 정돈합니다." },
        { "number": 3, "title": "팩으로 활용", "description": "면도 자극이 심한 U존이나 건조한 부위에 패드를 1분간 올려 진정시킵니다." },
        { "number": 4, "title": "남은 양 흡수", "description": "얼굴에 남은 토너 입자를 손끝으로 톡톡 두드려 피부에 머금게 합니다." }
      ],
      "tip": "패드로 얼굴을 너무 세게 문지르면 미세한 상처가 날 수 있으니 손목에 힘을 빼고 가볍게 지나가듯 사용하세요."
    },
    "mist": {
      "steps": [
        { "number": 1, "title": "거리 확보", "description": "얼굴에서 약 20cm 정도 거리를 두고 눈을 감은 채 대기합니다." },
        { "number": 2, "title": "전체 분사", "description": "얼굴 전체에 수분 안개가 내려앉는 느낌으로 2~3회 넓게 분사합니다." },
        { "description": "건조한 사과존과 당김이 느껴지는 U존 부위에 한 번 더 집중적으로 분사합니다.", "number": 3, "title": "부위별 보충" },
        { "number": 4, "title": "자연 흡수", "description": "분사된 입자가 날아가지 않도록 깨끗한 손바닥으로 T존부터 얼굴 전체를 가볍게 눌러줍니다." }
      ],
      "tip": "미스트 분사 후 그대로 방치하면 오히려 피부 수분이 같이 증발할 수 있으니 반드시 손으로 눌러 흡수시켜야 합니다."
    }
  },
  // 스킨케어 - 에센스·세럼
  "에센스·세럼": {
    "watery": {
      "steps": [
        { "number": 1, "title": "손바닥 덜기", "description": "물처럼 흐르는 제형이므로 500원 동전 크기로 덜어 양손에 가볍게 나눕니다." },
        { "number": 2, "title": "수분 충전", "description": "건조함이 가장 먼저 느껴지는 사과존을 중심으로 얼굴 전체를 감싸듯 발라줍니다." },
        { "number": 3, "title": "T존 레이어링", "description": "금방 건조해지는 T존 부위는 한 번 더 덧발라 수분 막을 겹겹이 쌓아줍니다." },
        { "number": 4, "title": "핸드 프레스", "description": "손바닥의 열기를 이용해 얼굴 전체를 지긋이 눌러 내용물을 깊숙이 밀착시킵니다." }
      ],
      "tip": "흡수가 빠른 워터 타입은 세안 직후 첫 단계에 사용하면 다음 단계 제품의 흡수력을 높여줍니다."
    },
    "oil": {
      "steps": [
        { "number": 1, "title": "적정량 펌핑", "description": "기름질 수 있으므로 1~2방울 정도만 덜어 손가락 끝에 묻힙니다." },
        { "number": 2, "title": "U존 중심 도포", "description": "쉽게 트고 갈라지는 U존과 입가 주변을 중심으로 톡톡 찍어 바릅니다." },
        { "number": 3, "title": "오일 코팅", "description": "사과존 위주로 얇게 펴 발라 수분이 증발하지 않도록 얇은 보호막을 씌워줍니다." },
        { "number": 4, "title": "번들거림 방지", "description": "유분이 많은 T존은 직접 바르지 말고 손에 남은 잔여량으로만 살짝 스치듯 지나갑니다." }
      ],
      "tip": "지성 피부라면 얼굴 전체보다는 건조한 부위에만 부분적으로 사용하는 것이 트러블 방지에 좋습니다."
    },
    "viscous": {
      "steps": [
        { "number": 1, "title": "직접 도포", "description": "점성이 있는 제형을 검지 한 마디 정도 덜어 이마와 양볼(사과존)에 직접 올립니다." },
        { "number": 2, "title": "롤링 마사지", "description": "피부 결을 따라 안쪽에서 바깥쪽으로 부드럽게 원을 그리며 펴 바릅니다." },
        { "number": 3, "title": "굴곡 부위 밀착", "description": "콧망울 옆 코 부위와 미간 사이의 좁은 틈까지 손가락으로 꼼꼼히 채워줍니다." },
        { "number": 4, "title": "두드려 흡수", "description": "끈적임이 남지 않도록 얼굴 전체를 30초 정도 가볍게 두드려 마무리합니다." }
      ],
      "tip": "점성이 높은 제품은 문지르기만 하면 겉돌 수 있으므로 충분히 두드려야 속보습까지 해결됩니다."
    }
  },
  // 스킨케어 - 로션·크림
  "로션·크림": {
    "lotion": {
      "steps": [
        { "number": 1, "title": "양 조절", "description": "손바닥에 500원 동전 크기로 덜어 양손으로 가볍게 펼칩니다." },
        { "number": 2, "title": "얼굴 도포", "description": "면도로 인해 건조해지기 쉬운 U존을 시작으로 사과존까지 부드럽게 펴 바릅니다." },
        { "number": 3, "title": "T존 흡수", "description": "상대적으로 유분이 많은 T존은 손바닥에 남은 양으로만 얇게 눌러줍니다." },
        { "number": 4, "title": "두드리기", "description": "피부 겉에 맴돌지 않도록 얼굴 전체를 톡톡 두드려 완전히 밀착시킵니다." }
      ],
      "tip": "로션은 유수분 밸런스를 맞추는 핵심 단계이므로, 번들거림이 싫더라도 최소한 U존만큼은 꼭 챙겨 발라야 합니다."
    },
    "cream": {
      "steps": [
        { "number": 1, "title": "내용물 덜기", "description": "검지 한 마디 정도 양을 덜어 얼굴의 주요 부위에 점을 찍듯 나눠 올립니다." },
        { "number": 2, "title": "보습막 형성", "description": "사과존과 눈가 주변을 안쪽에서 바깥쪽으로 원을 그리며 두툼하게 바릅니다." },
        { "number": 3, "title": "코·T존 연결", "description": "코 부위와 T존은 뭉치기 쉬우므로 손가락 끝을 이용해 얇고 고르게 펴줍니다." },
        { "number": 4, "title": "핸드 프레스", "description": "양손으로 얼굴 전체를 감싸 체온으로 크림을 깊숙이 흡수시켜 마무리합니다." }
      ],
      "tip": "크림은 보습의 마지막 단계이므로 잠들기 전 평소보다 조금 더 두껍게 바르면 다음 날 아침 피부가 훨씬 쫀쫀해집니다."
    },
    "gel": {
      "steps": [
        { "number": 1, "title": "적당량 도포", "description": "500원 동전 크기만큼 덜어 수분감이 느껴지도록 얼굴 전체에 펴 바릅니다." },
        { "number": 2, "title": "쿨링 마사지", "description": "열감이 느껴지는 사과존과 코 부위를 위주로 시원하게 롤링하며 마사지합니다." },
        { "number": 3, "title": "U존 진정", "description": "면도 직후 예민해진 U존 라인에 젤을 얹어 붉은 기를 가라앉힙니다." },
        { "number": 4, "title": "산뜻한 마무리", "description": "끈적임이 사라질 때까지 가볍게 두드려 산뜻하게 흡수시킵니다." }
      ],
      "tip": "유분기가 적고 수분이 많아 여름철이나 지성 피부인 분들이 답답함 없이 쓰기 가장 좋습니다."
    },
    "balm": {
      "steps": [
        { "number": 1, "title": "밤 녹이기", "description": "엄지손톱만큼 덜어 손바닥의 열기로 오일처럼 녹을 때까지 비벼줍니다." },
        { "number": 2, "title": "국소 부위 도포", "description": "하얗게 각질이 일어난 코 부위나 입가 주변 U존에 꾹꾹 누르듯 바릅니다." },
        { "number": 3, "title": "전체 코팅", "description": "손에 남은 잔여물을 사과존에 얇게 펴 발라 강력한 수분 보호막을 만듭니다." },
        { "number": 4, "title": "밀착 확인", "description": "번들거림이 심한 곳은 없는지 확인하며 손바닥으로 전체를 가볍게 눌러줍니다." }
      ],
      "tip": "제형이 무거우므로 한겨울이나 극건성 피부에 추천하며, 트러블 부위는 피해서 바르는 것이 좋습니다."
    }
  },
  // 스킨케어 - 선케어
  "선케어": {
    "cream": {
      "steps": [
        { "number": 1, "title": "양 조절", "description": "검지손가락 두 마디 정도 길게 짜거나 500원 동전 크기만큼 충분히 덜어냅니다." },
        { "number": 2, "title": "부위별 도포", "description": "자외선에 노출되기 쉬운 사과존과 코 부위를 시작으로 얼굴 전체에 점을 찍듯 나눠 바릅니다." },
        { "number": 3, "title": "T존 밀착", "description": "유분이 올라오기 쉬운 T존은 얇게 여러 번 두드려 밀착시키고 잔여물이 뭉치지 않게 펴 바릅니다." },
        { "number": 4, "title": "목 라인 마무리", "description": "손에 남은 양으로 턱선 아래 U존과 목 부분까지 쓸어내리듯 발라 경계가 생기지 않게 합니다." }
      ],
      "tip": "외출 20분 전에 발라야 차단막이 제대로 형성되며, 외출 후에는 폼 클렌저로 꼼꼼히 씻어내야 트러블을 막을 수 있습니다."
    },
    "stick": {
      "steps": [
        { "number": 1, "title": "스틱 준비", "description": "스틱 하단의 다이얼을 돌려 내용물을 5mm 정도만 위로 올라오게 꺼냅니다." },
        { "number": 2, "title": "중앙 마사지", "description": "면적이 넓은 사과존과 U존을 중심으로 안쪽에서 바깥쪽으로 3~4회 겹쳐서 슥슥 문지릅니다." },
        { "number": 3, "title": "굴곡진 부위", "description": "스틱의 모서리를 이용해 코 망울 옆과 T존의 좁은 부위까지 빈틈없이 덧바릅니다." },
        { "number": 4, "title": "결 정돈", "description": "손가락 끝으로 가볍게 두드려 스틱 자국을 없애고 피부에 완벽하게 밀착시킵니다." }
      ],
      "tip": "한 번만 슥 지나가면 차단 양이 부족할 수 있으니, 같은 자리를 여러 번 덧칠한다는 느낌으로 사용해야 효과적입니다."
    },
    "spray": {
      "steps": [
        { "number": 1, "title": "제품 흔들기", "description": "내용물이 잘 섞이도록 상하로 5회 이상 충분히 흔든 후 얼굴에서 20cm 정도 거리를 둡니다." },
        { "number": 2, "title": "전체 분사", "description": "눈을 감고 얼굴 전체에 원을 그리듯 2~3초간 골고루 분사합니다." },
        { "number": 3, "title": "취약 부위 보충", "description": "햇볕을 직접 받는 코 부위와 사과존에 안개가 내려앉듯 한 번 더 가볍게 뿌려줍니다." },
        { "number": 4, "title": "흡수 및 밀착", "description": "피부 표면에 맺힌 입자가 흡수되도록 T존과 U존을 손바닥으로 가볍게 눌러 마무리합니다." }
      ],
      "tip": "코로 흡입하지 않도록 숨을 참은 채 분사하고, 바람이 많이 부는 야외에서는 손바닥에 뿌려 얼굴에 바르는 것이 더 정확합니다."
    }
  },
  // 스킨케어 - 기타
  "기타": {
    "sheet": {
      "steps": [
        { "number": 1, "title": "위치 맞추기", "description": "눈과 코 부위의 구멍을 먼저 맞춘 뒤 얼굴 전체에 들뜸 없이 밀착시킵니다." },
        { "number": 2, "title": "사과존 밀착", "description": "시트가 뜨기 쉬운 사과존과 턱 라인을 손가락으로 쓸어 공기를 빼줍니다." },
        { "number": 3, "title": "시간 엄수", "description": "15분 정도 부착한 뒤 시트가 마르기 전에 아래에서 위로 떼어냅니다." },
        { "number": 4, "title": "잔여액 흡수", "description": "얼굴에 남은 에센스를 톡톡 두드려 피부 속까지 밀어 넣습니다." }
      ],
      "tip": "마스크팩을 아깝다고 20분 넘게 붙이고 있으면 오히려 시트가 피부 수분을 다시 뺏어가니 주의하세요."
    },
    "patch": {
      "steps": [
        { "number": 1, "title": "부위 청결", "description": "트러블 부위를 깨끗이 세안하고 물기를 완벽하게 제거합니다." },
        { "number": 2, "title": "패치 부착", "description": "트러블 크기에 맞는 패드를 골라 정중앙에 위치하게 붙입니다." },
        { "number": 3, "title": "가장자리 압착", "description": "패치가 들뜨지 않도록 손가락 끝으로 3초간 지긋이 눌러 밀착시킵니다." },
        { "number": 4, "title": "교체 시기", "description": "패치가 하얗게 부풀어 오르면 조심스럽게 떼어내고 새것으로 교체합니다." }
      ],
      "tip": "트러블을 손으로 짜고 나면 흉터가 남으니, 손대지 말고 바로 패치를 붙여 보호하는 게 상책입니다."
    },
    "massage": {
      "steps": [
        { "number": 1, "title": "윤활제 도포", "description": "피부 마찰을 줄이기 위해 로션이나 오일을 얼굴 전체에 충분히 바릅니다." },
        { "number": 2, "title": "라인 리프팅", "description": "턱 끝 U존부터 귀 아래까지 근육을 끌어올리듯 손가락으로 쓸어줍니다." },
        { "number": 3, "title": "노폐물 배출", "description": "콧망울 옆 코 부위에서 사과존 방향으로 광대 라인을 따라 부드럽게 누릅니다." },
        { "number": 4, "title": "마무리 스트레칭", "description": "관자놀이와 목선을 따라 아래로 쓸어내려 혈액순환을 돕고 정리합니다." }
      ],
      "tip": "맨얼굴에 하면 피부가 다 늘어날 수 있으니, 무조건 미끄러운 제품을 듬뿍 바른 상태에서 하세요."
    }
  },
  // 뷰티 (초안)
  "베이스 메이크업": {
    "foundation": {
      "steps": [
        { "number": 1, "title": "소량 덜기", "description": "손등에 50원 동전 크기만큼 덜어낸 뒤 손가락으로 가볍게 찍어줍니다." },
        { "number": 2, "title": "중앙 도포", "description": "면적이 넓은 사과존부터 시작해 콧등과 이마 순으로 얇게 점을 찍듯 올립니다." },
        { "number": 3, "title": "바깥쪽 블렌딩", "description": "손가락이나 퍼프를 이용해 안쪽에서 바깥쪽으로 경계가 생기지 않게 펴 바릅니다." },
        { "number": 4, "title": "외곽 정리", "description": "턱 라인 U존은 손에 남은 양으로만 살살 쓸어내려 목과 얼굴의 색 차이를 없앱니다." }
      ],
      "tip": "파운데이션은 얼굴 전체를 다 덮는 게 아니라, 중앙 위주로 바르고 외곽은 자연스럽게 흐려지게 해야 '화장한 티'가 안 납니다."
    },
    "cushion": {
      "steps": [
        { "number": 1, "title": "퍼프 양 조절", "description": "내용물을 퍼프에 살짝 찍은 뒤 뚜껑 뒷면에 두드려 양을 고르게 조절합니다." },
        { "number": 2, "title": "두드리기", "description": "사과존부터 시작해 가볍게 톡톡 두드리며 얼굴 전체에 밀착시킵니다." },
        { "number": 3, "title": "모공 커버", "description": "코 부위는 퍼프를 살짝 접어 굴곡진 곳까지 꼼꼼하게 밀어 넣듯 두드려줍니다." },
        { "number": 4, "title": "밀착 확인", "description": "T존이나 눈가에 내용물이 뭉치지 않았는지 확인하며 전체적으로 한 번 더 눌러줍니다." }
      ],
      "tip": "밀어서 바르면 모공이 다 보이고 화장이 금방 뜨니까, 무조건 '톡톡톡' 수직으로 두드리는 게 생명입니다."
    },
    "bb_cc": {
      "steps": [
        { "number": 1, "title": "적정량 덜기", "description": "검지 한 마디 정도를 덜어 양 볼(사과존)과 이마, 코에 나눠 올립니다." },
        { "number": 2, "title": "결 따라 펴바르기", "description": "로션을 바르듯 손바닥 전체를 이용해 안쪽에서 바깥쪽으로 부드럽게 펴 줍니다." },
        { "number": 3, "title": "잡티 보정", "description": "수염 자국이 두드러지는 인중과 U존 부위는 조금 더 세밀하게 덧발라줍니다." },
        { "number": 4, "title": "경계선 정리", "description": "헤어라인과 눈썹 근처에 내용물이 끼지 않았는지 손가락으로 펴서 마무리합니다." }
      ],
      "tip": "비비크림은 시간이 지나면 회색빛이 돌 수 있으니, 아주 얇게 바르는 게 가장 자연스럽습니다."
    },
    "stick": {
      "steps": [
        { "number": 1, "title": "가이드라인", "description": "스틱을 돌려 올린 뒤 사과존에 두 줄, 이마에 한 줄 가볍게 선을 긋습니다." },
        { "number": 2, "title": "중앙 마사지", "description": "손가락이나 브러쉬를 이용해 그어놓은 선을 밖으로 펼치며 넓게 펴 바릅니다." },
        { "number": 3, "title": "코·입가 세밀 커버", "description": "스틱 모서리를 이용해 코 옆 붉은 기와 입가 어두운 곳을 톡톡 찍어 가립니다." },
        { "number": 4, "title": "두드려 고정", "description": "얼굴 전체를 손바닥으로 감싸 밀착력을 높이고 번들거림을 잡습니다." }
      ],
      "tip": "얼굴에 직접 긋는 방식이라 양 조절이 실패하기 쉬우니, 처음에는 아주 연하게 긋고 부족하면 덧바르세요."
    }
  },
  "컨실링": {
    "liquid_concealer": {
      "steps": [
        { "number": 1, "title": "결점 부착", "description": "가리고 싶은 다크서클이나 옅은 잡티 부위에 소량만 톡 찍어 올립니다." },
        { "number": 2, "title": "대기하기", "description": "바로 문지르지 말고 10초 정도 기다려 제형이 피부에 살짝 고정되게 둡니다." },
        { "number": 3, "title": "경계 블렌딩", "description": "결점 중앙이 아닌 가장자리만 손가락으로 톡톡 두드려 베이스와 연결합니다." },
        { "number": 4, "title": "자연스러운 마무리", "description": "사과존 주변의 피부 톤과 이질감이 없는지 확인하며 가볍게 눌러줍니다." }
      ],
      "tip": "다크서클 가릴 때 너무 밝은 색을 쓰면 오히려 눈 밑만 둥둥 떠 보이니 본인 피부보다 반 톤 어두운 게 안전합니다."
    },
    "stick_concealer": {
      "steps": [
        { "number": 1, "title": "직접 터치", "description": "진한 여드름 흉터나 점 위에 스틱을 세워 콕 찍어줍니다." },
        { "number": 2, "title": "밀착 압박", "description": "손가락 끝으로 패치 붙이듯 지긋이 눌러 결점을 완벽히 덮습니다." },
        { "number": 3, "title": "주변부 정리", "description": "결점 바로 위는 건드리지 말고 주변만 살살 펴서 경계를 없앱니다." },
        { "number": 4, "title": "고정 확인", "description": "건조함이 심한 U존 부위라면 갈라지지 않았는지 확인하며 마무리합니다." }
      ],
      "tip": "수염 자국을 가릴 때 스틱 컨실러를 쓰면 아주 효과적이지만, 너무 두꺼워지면 인위적이니 주의하세요."
    },
    "pot_concealer": {
      "steps": [
        { "number": 1, "title": "제형 녹이기", "description": "손가락 끝의 체온으로 밤 제형을 살짝 녹여 소량만 묻힙니다." },
        { "number": 2, "title": "요철 메우기", "description": "패인 흉터나 굴곡이 있는 코 부위에 꾹꾹 누르듯 발라 빈틈을 채웁니다." },
        { "number": 3, "title": "중첩 커버", "description": "커버가 더 필요한 부분만 한 번 더 얇게 쌓아 올립니다." },
        { "number": 4, "title": "표면 정돈", "description": "파우더를 바른 듯 매트하게 마무리되도록 가볍게 두드려줍니다." }
      ],
      "tip": "커버력이 가장 높지만 그만큼 뻑뻑해서 넓은 부위에 바르면 화장이 쩍쩍 갈라질 수 있습니다."
    }
  },
  "아이브로우": {
    "pencil": {
      "steps": [
        { "number": 1, "title": "가이드 잡기", "description": "눈썹 아래 라인을 먼저 직선으로 그려 눈썹의 전체적인 모양을 잡습니다." },
        { "number": 2, "title": "빈 곳 채우기", "description": "눈썹 숱이 부족한 곳을 한 올 한 올 심는다는 느낌으로 짧게 끊어 그립니다." },
        { "number": 3, "title": "눈썹 꼬리 정리", "description": "눈썹 끝부분을 깔끔하게 빼주어 인상을 선명하게 만듭니다." },
        { "number": 4, "title": "스크류 브러쉬", "description": "뒤편의 솔로 앞머리부터 뒤쪽까지 빗어주며 진한 부분을 자연스럽게 풀어줍니다." }
      ],
      "tip": "눈썹 앞머리를 너무 진하게 그리면 '짱구'가 되기 십상이니, 뒤쪽 위주로 그리고 앞은 남은 양으로만 빗으세요."
    },
    "powder": {
      "steps": [
        { "number": 1, "title": "색상 믹스", "description": "브러쉬에 파우더를 묻혀 손등에서 양을 조절한 뒤 색상을 섞어줍니다." },
        { "number": 2, "title": "면 채우기", "description": "눈썹 중앙부터 시작해 빈 공간을 색칠하듯 부드럽게 채워 나갑니다." },
        { "number": 3, "title": "모양 다듬기", "description": "브러쉬에 남은 양으로 눈썹 위아래 라인을 살짝 잡아 정돈합니다." },
        { "number": 4, "title": "고정", "description": "가루가 날리지 않도록 손가락 끝으로 한 번 지긋이 눌러 마무리합니다." }
      ],
      "tip": "펜슬보다 훨씬 자연스러워서 눈썹 그리는 게 서툰 초보자 남성분들에게 가장 추천하는 아이템입니다."
    },
    "brow_cara": {
      "steps": [
        { "number": 1, "title": "입구 양 조절", "description": "브러쉬를 꺼낼 때 입구에서 내용물을 덜어내어 뭉치지 않게 조절합니다." },
        { "number": 2, "title": "역방향 빗질", "description": "눈썹 결 반대 방향으로 가볍게 빗어 눈썹 모 안쪽까지 색을 입힙니다." },
        { "number": 3, "title": "결 정돈", "description": "다시 정방향으로 빗으며 눈썹 앞머리는 세우고 꼬리는 눕혀 모양을 잡습니다." },
        { "number": 4, "title": "건조", "description": "액이 완전히 마를 때까지 손대지 않고 고정될 때까지 기다립니다." }
      ],
      "tip": "피부에 닿으면 지우기 힘드니까 살에는 안 닿게 눈썹 '털'만 빗어준다는 느낌으로 하세요."
    }
  },
  "립": {
    "lip_balm": {
      "steps": [
        { "number": 1, "title": "입술 정리", "description": "침을 바르지 않은 마른 상태의 입술을 준비합니다." },
        { "number": 2, "title": "안쪽부터 도포", "description": "입술 중앙을 중심으로 위아래 가볍게 슥슥 문질러 바릅니다." },
        { "number": 3, "title": "입술 산 정리", "description": "손가락으로 입술 라인 경계를 톡톡 두드려 색이 자연스럽게 퍼지게 합니다." },
        { "number": 4, "title": "보습 확인", "description": "음파음파를 한 번 하여 위아래 입술에 제형이 고루 묻게 합니다." }
      ],
      "tip": "빨간 립스틱 같은 느낌이 아니라 '생기 있는 입술'을 만드는 게 목표입니다. 거울 보고 입술 라인만 잘 펴주세요."
    },
    "tint": {
      "steps": [
        { "number": 1, "title": "입구 덜기", "description": "팁에 묻은 양을 입구에서 최대한 덜어내어 소량만 남깁니다." },
        { "number": 2, "title": "중앙 점찍기", "description": "아래 입술 안쪽에만 콕콕 세 번 정도 점을 찍듯 바릅니다." },
        { "number": 3, "title": "빠른 블렌딩", "description": "액이 스며들기 전에 손가락으로 입술 전체에 펴 발라 그라데이션을 만듭니다." },
        { "number": 4, "title": "밀착 고정", "description": "입술 주변에 묻은 액을 닦아내고 완전히 스며들 때까지 기다립니다." }
      ],
      "tip": "입술 전체에 다 바르면 쥐 잡아먹은 듯 보일 수 있으니 무조건 안쪽에서 시작해서 밖으로 넓히세요."
    },
    "lip_stick": {
      "steps": [
        { "number": 1, "title": "직접 도포", "description": "스틱을 조금만 올려 입술 전체에 얇게 한 번 펴 바릅니다." },
        { "number": 2, "title": "두드리기", "description": "손가락을 이용해 입술 표면을 톡톡 두드려 광택을 죽이고 매트하게 만듭니다." },
        { "number": 3, "title": "윤곽 보정", "description": "입술 꼬리 부분까지 색이 고르게 전달되도록 꼼꼼히 정리합니다." },
        { "number": 4, "title": "잔여물 제거", "description": "휴지에 입술을 살짝 찍어내어 너무 과한 색감을 덜어냅니다." }
      ],
      "tip": "남성용 립스틱은 보통 매트한 게 많으니 바르기 전에 립밤으로 각질을 미리 잠재워둬야 안 지저분합니다."
    }
  },
  "쉐딩·하이라이터": {
    "powder_shading": {
      "steps": [
        { "number": 1, "title": "브러쉬 털기", "description": "브러쉬에 가루를 묻힌 뒤 허공에 툭툭 털어 과한 양을 제거합니다." },
        { "number": 2, "title": "턱선 깎기", "description": "귀 아래 턱선 U존부터 턱 끝까지 뒤에서 앞으로 쓸어 음영을 줍니다." },
        { "number": 3, "title": "콧대 세우기", "description": "작은 브러쉬로 눈썹 앞머리부터 코 옆 라인을 따라 얇게 음영을 넣습니다." },
        { "number": 4, "title": "자연스러운 연결", "description": "경계선이 보이지 않게 빈 브러쉬로 주변을 한 번 더 쓸어 정리합니다." }
      ],
      "tip": "조명 아래서 봤을 때 옆모습에 줄이 가 있으면 망한 겁니다. 턱 밑 어두운 곳까지 충분히 풀어주세요."
    },
    "stick_shading": {
      "steps": [
        { "number": 1, "title": "직선 긋기", "description": "콧대 옆과 턱선 라인을 따라 스틱으로 얇게 한 줄을 긋습니다." },
        { "number": 2, "title": "경계 허물기", "description": "손가락이나 스펀지로 선이 보이지 않을 때까지 충분히 두드려 폅니다." },
        { "number": 3, "title": "헤어라인 보정", "description": "M자 이마가 고민이라면 이마 구석 부위에 슥 긋고 머리카락 쪽으로 폅니다." },
        { "number": 4, "title": "고정 확인", "description": "음영이 너무 진하지 않은지 거울을 멀리서 확인하며 마무리합니다." }
      ],
      "tip": "스틱은 파우더보다 발색이 강해서 펴바르는 걸 대충 하면 얼굴에 검은 칠 한 것처럼 보일 수 있습니다."
    },
    "highlighter": {
      "steps": [
        { "number": 1, "title": "믹싱 준비", "description": "파운데이션과 하이라이터 액상을 2:1 비율로 손등에서 섞습니다." },
        { "number": 2, "title": "T존 입체감", "description": "콧대 중앙과 이마 중앙 부위에 얇게 펴 발라 콧대를 높아 보이게 합니다." },
        { "number": 3, "title": "사과존 광택", "description": "눈밑 사과존 윗부분에 살짝 얹어 건강해 보이는 피부 광을 만듭니다." },
        { "number": 4, "title": "밀착 마무리", "description": "경계가 생기지 않게 퍼프로 가볍게 두드려 베이스와 하나가 되게 합니다." }
      ],
      "tip": "펄이 너무 빤짝거리는 건 피하세요. 남성용은 '광'보다는 '피부가 좋아 보이는 정도'면 충분합니다."
    }
  }
}

export default function GuideDetailPage() {
  const { category, productType } = useParams<{ category: string; productType: string }>()
  const navigate = useNavigate()
  
  // Get subtypes based on productType, fallback to default
  const contentTypes = PRODUCT_SUBTYPES[productType || ""] || PRODUCT_SUBTYPES["default"]
  
  // State 1: Face Area (Controls AR/Masking only)
  const [selectedArea, setSelectedArea] = useState<string>("t-zone")
  
  // State 2: Content Type (Controls Text Guide)
  // Initialize with the first available type's id
  const [selectedContent, setSelectedContent] = useState<string>(contentTypes[0]?.id || "")

  // Update selectedContent when productType changes (or contentTypes changes)
  useEffect(() => {
    if (contentTypes.length > 0) {
      setSelectedContent(contentTypes[0].id)
    }
  }, [productType])

  // Get steps for current selection
  const getContent = () => {
    // Logic: productType -> selectedContent -> Steps
    const productSteps = usageSteps[productType || "default"] || usageSteps.default
    return productSteps[selectedContent] || productSteps["type1"] || { steps: [], tip: "" }
  }

  const { steps, tip } = getContent()

  return (
    <div className="min-h-screen bg-background">
      {/* Header (Hero Section) */}
      <section className="bg-gradient-to-br from-pastel-blue-100/50 via-background to-primary-100/30 py-8 md:py-12">
        <div className="mx-auto max-w-[1400px] px-6">
          <button
            onClick={() => navigate(-1)}
            className="inline-flex items-center gap-2 text-text-secondary hover:text-primary-500 transition-colors mb-6"
          >
            <ArrowLeft className="h-5 w-5" />
            <span className="font-medium">뒤로 가기</span>
          </button>

          <div className="text-center">
            <h1 className="text-3xl md:text-4xl font-bold text-text-primary mb-4">
              {productType} 사용 가이드
            </h1>
            <p className="text-text-secondary">
              {category === "skincare" && "스킨케어"}
              {category === "beauty" && "뷰티"}
            </p>
          </div>
        </div>
      </section>

      {/* NEW: Content Selection Section (Horizontal Buttons) */}
      <section className="border-b border-border bg-background sticky top-0 z-10 shadow-sm">
        <div className="mx-auto max-w-[1400px] px-6">
          <div className="flex items-center justify-center gap-4 py-4 overflow-x-auto no-scrollbar">
            {contentTypes.map((type) => (
              <button
                key={type.id}
                onClick={() => setSelectedContent(type.id)}
                className={`
                  px-6 py-2.5 rounded-full text-sm font-semibold whitespace-nowrap transition-all
                  ${
                    selectedContent === type.id
                      ? "bg-primary-500 text-white shadow-md"
                      : "bg-muted text-text-secondary hover:bg-muted/80"
                  }
                `}
              >
                {type.label}
              </button>
            ))}
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="mx-auto max-w-[1400px] px-6 py-12 md:py-16">
        <div className="bg-muted/30 rounded-2xl p-6 md:p-8">
          <div className="grid lg:grid-cols-2 gap-8 lg:gap-12">
            {/* Left: Face Camera Section (Controls Masking Only) */}
            <div>
              <FaceCameraSection
                selectedArea={selectedArea}
                onAreaSelect={setSelectedArea}
              />
            </div>

            {/* Right: Step Guide (Controlled by Buttons above) */}
            <div>
              <StepGuide steps={steps} tip={tip} />
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="bg-gradient-to-r from-pastel-purple-100 to-pastel-green-100 py-16">
        <div className="mx-auto max-w-[1200px] px-6 text-center">
          <h2 className="text-2xl font-bold text-text-primary mb-4">
            제품 사용에 대해 더 궁금하신가요?
          </h2>
          <p className="text-text-secondary mb-8 max-w-2xl mx-auto">
            전문 멘토와 1:1 상담을 통해 피부 타입에 맞는 맞춤형 제품 사용법을 추천받아보세요.
          </p>
          <a
            href="/consultation"
            className="inline-flex items-center gap-2 rounded-xl bg-dark-bg px-8 py-3.5 font-medium text-primary-500 transition-all hover:bg-dark-bg/90"
          >
            상담 예약하기
          </a>
        </div>
      </section>
    </div>
  )
}

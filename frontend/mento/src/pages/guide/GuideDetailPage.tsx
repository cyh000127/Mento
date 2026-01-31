import { useParams, useNavigate } from "react-router-dom"
import { useState } from "react"
import { FaceAreaSelector } from "@/components/guide/FaceAreaSelector"
import { StepGuide } from "@/components/guide/StepGuide"
import { ArrowLeft } from "lucide-react"

// Usage steps per product type and face area
const usageSteps: Record<string, Record<string, any[]>> = {
  튜브: {
    forehead: [
      {
        number: 1,
        title: "적당량 짜내기",
        description: "튜브를 가볍게 눌러 손가락 끝 크기만큼 제품을 짜냅니다.",
      },
      {
        number: 2,
        title: "이마 중앙에서 바깥쪽으로",
        description: "이마 중앙에서 시작해 관자놀이 방향으로 부드럽게 펴 발라줍니다.",
      },
      {
        number: 3,
        title: "두드려 흡수시키기",
        description: "손가락 끝으로 가볍게 두드리며 피부에 흡수시켜 줍니다.",
      },
      {
        number: 4,
        title: "마무리",
        description: "완전히 흡수될 때까지 1-2분 정도 기다려 줍니다.",
      },
    ],
    nose: [
      {
        number: 1,
        title: "소량 짜내기",
        description: "코 부위는 소량만 사용합니다. 작은 쌀알 크기 정도로 짜냅니다.",
      },
      {
        number: 2,
        title: "위에서 아래로",
        description: "콧등에서 코끝 방향으로 부드럽게 펴 발라줍니다.",
      },
      {
        number: 3,
        title: "코 옆 세심하게",
        description: "각질이 쌓이기 쉬운 콧방울 주변을 꼼꼼히 케어합니다.",
      },
      {
        number: 4,
        title: "흡수 확인",
        description: "피부가 촉촉하게 정돈될 때까지 기다립니다.",
      },
    ],
    cheeks: [
      {
        number: 1,
        title: "적당량 손에 덜기",
        description: "양 볼에 나눠 바를 만큼의 제품을 짜냅니다.",
      },
      {
        number: 2,
        title: "안쪽에서 바깥쪽으로",
        description: "볼 안쪽에서 바깥쪽으로 원을 그리며 부드럽게 펴 발라줍니다.",
      },
      {
        number: 3,
        title: "가볍게 두드리기",
        description: "손바닥으로 볼을 감싸 가볍게 두드리며 흡수를 도와줍니다.",
      },
      {
        number: 4,
        title: "보습 확인",
        description: "건조함이 느껴지지 않을 때까지 충분히 흡수시킵니다.",
      },
    ],
    jaw: [
      {
        number: 1,
        title: "적당량 준비",
        description: "턱선 전체에 바를 만큼의 제품을 짜냅니다.",
      },
      {
        number: 2,
        title: "턱 중앙에서 귀 방향으로",
        description: "턱 중앙에서 시작해 귀 쪽으로 쓸어올리듯 발라줍니다.",
      },
      {
        number: 3,
        title: "목까지 연결",
        description: "턱선에서 목으로 자연스럽게 이어지도록 펴 발라줍니다.",
      },
      {
        number: 4,
        title: "마무리 마사지",
        description: "림프 순환을 위해 턱선을 따라 가볍게 마사지합니다.",
      },
    ],
  },
  펌프: {
    forehead: [
      {
        number: 1,
        title: "펌프 1-2회 누르기",
        description: "손바닥에 펌프를 1-2회 눌러 적당량을 덜어냅니다.",
      },
      {
        number: 2,
        title: "손으로 체온 데우기",
        description: "양 손바닥을 비벼 제품을 체온으로 따뜻하게 합니다.",
      },
      {
        number: 3,
        title: "이마에 점 찍듯 배치",
        description: "이마 여러 부위에 점 찍듯 배치한 후 펴 발라줍니다.",
      },
      {
        number: 4,
        title: "흡수 도우기",
        description: "손바닥으로 이마를 감싸고 5-10초간 밀착시켜 흡수를 돕습니다.",
      },
    ],
    nose: [
      {
        number: 1,
        title: "극소량 사용",
        description: "코는 피지 분비가 많으므로 펌프 반회분만 사용합니다.",
      },
      {
        number: 2,
        title: "콧등 중심으로",
        description: "콧등 중앙에 소량을 바르고 코 전체에 얇게 펴줍니다.",
      },
      {
        number: 3,
        title: "가볍게 두드리기",
        description: "손가락 끝으로 톡톡 두드리며 흡수시킵니다.",
      },
      {
        number: 4,
        title: "과도한 유분 제거",
        description: "필요시 티슈로 가볍게 눌러 과도한 유분을 제거합니다.",
      },
    ],
    cheeks: [
      {
        number: 1,
        title: "적정량 준비",
        description: "양 볼에 충분한 양의 제품을 펌핑합니다.",
      },
      {
        number: 2,
        title: "볼 중앙에 배치",
        description: "양 볼 중앙에 제품을 놓고 바깥쪽으로 펴줍니다.",
      },
      {
        number: 3,
        title: "리프팅 마사지",
        description: "아래에서 위로 쓸어올리며 리프팅 효과를 줍니다.",
      },
      {
        number: 4,
        title: "핸드 프레스",
        description: "손바닥으로 볼을 감싸고 체온으로 흡수를 도와줍니다.",
      },
    ],
    jaw: [
      {
        number: 1,
        title: "적당량 덜기",
        description: "턱선과 목 부위에 사용할 제품을 덜어냅니다.",
      },
      {
        number: 2,
        title: "턱선 따라 바르기",
        description: "턱선을 따라 귀 뒤쪽까지 부드럽게 펴 발라줍니다.",
      },
      {
        number: 3,
        title: "목까지 연장",
        description: "목 부위까지 자연스럽게 이어서 발라줍니다.",
      },
      {
        number: 4,
        title: "림프 마사지",
        description: "턱에서 귀 아래로 쓸어내리며 림프 순환을 도와줍니다.",
      },
    ],
  },
  // Default steps for other products
  default: {
    forehead: [
      {
        number: 1,
        title: "제품 덜어내기",
        description: "적당량을 손바닥에 덜어냅니다.",
      },
      {
        number: 2,
        title: "이마에 바르기",
        description: "이마 중앙에서 바깥쪽으로 부드럽게 펴 발라줍니다.",
      },
      {
        number: 3,
        title: "흡수시키기",
        description: "가볍게 두드리며 피부에 흡수시켜 줍니다.",
      },
      {
        number: 4,
        title: "마무리",
        description: "완전히 흡수될 때까지 기다려 줍니다.",
      },
    ],
    nose: [
      {
        number: 1,
        title: "제품 덜어내기",
        description: "소량을 손바닥에 덜어냅니다.",
      },
      {
        number: 2,
        title: "코에 바르기",
        description: "콧등에서 코끝 방향으로 부드럽게 발라줍니다.",
      },
      {
        number: 3,
        title: "흡수시키기",
        description: "가볍게 두드리며 흡수시켜 줍니다.",
      },
      {
        number: 4,
        title: "마무리",
        description: "완전히 흡수될 때까지 기다려 줍니다.",
      },
    ],
    cheeks: [
      {
        number: 1,
        title: "제품 덜어내기",
        description: "적당량을 손바닥에 덜어냅니다.",
      },
      {
        number: 2,
        title: "볼에 바르기",
        description: "안쪽에서 바깥쪽으로 원을 그리며 펴 발라줍니다.",
      },
      {
        number: 3,
        title: "흡수시키기",
        description: "손바닥으로 감싸 가볍게 두드리며 흡수시킵니다.",
      },
      {
        number: 4,
        title: "마무리",
        description: "충분히 흡수될 때까지 기다려 줍니다.",
      },
    ],
    jaw: [
      {
        number: 1,
        title: "제품 덜어내기",
        description: "적당량을 손바닥에 덜어냅니다.",
      },
      {
        number: 2,
        title: "턱에 바르기",
        description: "턱 중앙에서 귀 방향으로 펴 발라줍니다.",
      },
      {
        number: 3,
        title: "흡수시키기",
        description: "가볍게 마사지하며 흡수시킵니다.",
      },
      {
        number: 4,
        title: "마무리",
        description: "완전히 흡수될 때까지 기다려 줍니다.",
      },
    ],
  },
}

export default function GuideDetailPage() {
  const { category, productType } = useParams<{ category: string; productType: string }>()
  const navigate = useNavigate()
  const [selectedArea, setSelectedArea] = useState<string>("forehead")

  // Get steps for current selection
  const getSteps = () => {
    if (!productType) return usageSteps.default.forehead
    const productSteps = usageSteps[productType] || usageSteps.default
    return productSteps[selectedArea] || usageSteps.default[selectedArea]
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
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
              {category === "hair" && "헤어"} 제품의 올바른 사용법을 확인하세요
            </p>
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="mx-auto max-w-[1400px] px-6 py-12 md:py-16">
        <div className="bg-muted/30 rounded-2xl p-6 md:p-8">
          <div className="grid lg:grid-cols-2 gap-8 lg:gap-12">
            {/* Left: Face Area Selector */}
            <div>
              <FaceAreaSelector
                selectedArea={selectedArea}
                onAreaSelect={setSelectedArea}
              />
            </div>

            {/* Right: Step Guide */}
            <div>
              <StepGuide steps={getSteps()} />
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

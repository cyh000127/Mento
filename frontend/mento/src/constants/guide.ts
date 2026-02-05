import { Droplets, Scissors, Sparkles, Wind } from "lucide-react"
import type { Category, FaceArea, Guide } from "../types/guide"

export const CATEGORIES: Category[] = [
  {
    id: "skincare",
    label: "스킨케어",
    title: "스킨케어 기초",
    description: "세안부터 보습까지, 피부 관리의 기본을 배웁니다.",
    icon: Droplets,
    articles: 24,
    gradient: "from-primary-400 to-primary-500",
    bgColor: "bg-primary-100/50",
  },
  {
    id: "haircare",
    label: "헤어케어", // Mapped from 'hair' in sidebar if needed, checking consistency
    title: "헤어케어",
    description: "두피 건강과 헤어 스타일링의 모든 것을 알아봅니다.",
    icon: Scissors,
    articles: 18,
    gradient: "from-pastel-purple-200 to-pastel-purple-100",
    bgColor: "bg-pastel-purple-100/50",
  },
  {
    id: "grooming",
    label: "뷰티", // Mapped from 'beauty'
    title: "그루밍 루틴",
    description: "매일 실천할 수 있는 효과적인 루틴을 설계합니다.",
    icon: Sparkles,
    articles: 15,
    gradient: "from-pastel-green-200 to-pastel-green-100",
    bgColor: "bg-pastel-green-100/50",
  },
  {
    id: "fragrance",
    label: "향수", 
    title: "향수 가이드",
    description: "상황별 향수 선택과 올바른 사용법을 익힙니다.",
    icon: Wind,
    articles: 12,
    gradient: "from-pastel-blue-200 to-pastel-blue-100",
    bgColor: "bg-pastel-blue-100/50",
  },
]

// For Sidebar (simplified view)
export const SIDEBAR_CATEGORIES: Category[] = [
  {
    id: "skincare",
    label: "스킨케어",
    icon: Droplets,
  },
  {
    id: "beauty",
    label: "뷰티",
    icon: Sparkles,
  },
  {
    id: "hair",
    label: "헤어",
    icon: Scissors,
  },
]

export const FACE_AREAS: FaceArea[] = [
  { id: "forehead", label: "이마", top: "15%", left: "50%" },
  { id: "nose", label: "코", top: "45%", left: "50%" },
  { id: "cheeks", label: "볼", top: "50%", left: "25%" },
  { id: "jaw", label: "턱", top: "75%", left: "50%" },
]

export const CAMERA_FACE_AREAS: FaceArea[] = [
  { id: "t-zone", label: "T-zone" },
  { id: "u-zone", label: "U-zone" },
  { id: "nose", label: "Nose zone" },
  { id: "apple", label: "Apple zone" },
]

export const PRODUCT_TYPES: Record<string, string[]> = {
  skincare: ["클렌징", "토너·스킨", "에센스·세럼", "로션·크림", "선케어", "기타"],
  beauty: ["베이스 메이크업", "컨실링", "아이브로우", "립", "쉐딩·하이라이터", "기타"],
}

export const FEATURED_GUIDES: Guide[] = [
  {
    id: 1,
    title: "완벽한 세안의 기술: 남성 피부를 위한 클렌징 가이드",
    category: "스킨케어",
    readTime: "5분",
    views: 12430,
    level: "입문",
    excerpt:
      "올바른 세안법은 스킨케어의 첫 걸음입니다. 이중 세안의 필요성부터 물 온도, 세안제 선택까지 기초부터 알아봅니다.",
  },
  {
    id: 2,
    title: "피부 타입별 토너 선택법",
    category: "스킨케어",
    readTime: "7분",
    views: 8923,
    level: "기초",
    excerpt:
      "건성, 지성, 복합성, 민감성 피부 타입에 따라 토너 선택 기준이 달라집니다. 나에게 맞는 토너를 찾는 방법을 알아봅니다.",
  },
  {
    id: 3,
    title: "남성 헤어 스타일링 완전 정복",
    category: "헤어케어",
    readTime: "10분",
    views: 15672,
    level: "기초",
    excerpt:
      "왁스, 포마드, 젤의 차이점과 얼굴형에 맞는 헤어 스타일 선택법. 드라이 기술까지 모두 다룹니다.",
  },
  {
    id: 4,
    title: "자외선 차단제, 제대로 알고 바르자",
    category: "스킨케어",
    readTime: "6분",
    views: 11245,
    level: "입문",
    excerpt:
      "SPF, PA의 의미부터 올바른 도포량, 재도포 타이밍까지. 선크림에 대한 모든 것을 정리했습니다.",
  },
  {
    id: 5,
    title: "일주일 그루밍 루틴 설계하기",
    category: "그루밍",
    readTime: "8분",
    views: 9876,
    level: "중급",
    excerpt:
      "매일 해야 할 것과 주 1-2회 해야 할 것을 구분하여 효율적인 그루밍 루틴을 만드는 방법을 소개합니다.",
  },
  {
    id: 6,
    title: "첫 향수 구매 가이드",
    category: "향수",
    readTime: "9분",
    views: 7654,
    level: "입문",
    excerpt:
      "향수 구매가 처음이라면? 기본 용어부터 시향 방법, 예산별 추천까지 초보자를 위한 완벽 가이드.",
  },
]

export const PRODUCT_SUBTYPES: Record<string, { id: string; label: string }[]> = {
  // 스킨케어
  "클렌징": [
    { id: "foam", label: "폼 클렌저" },
    { id: "oil", label: "클렌징 오일" },
    { id: "water", label: "클렌징 워터" },
    { id: "balm", label: "클렌징 밤" },
    { id: "pad", label: "클렌징 패드" },
    { id: "gel", label: "클렌징 젤" },
  ],
  "토너·스킨": [
    { id: "water", label: "워터 타입" },
    { id: "essence", label: "에센스 타입" },
    { id: "pad", label: "토너 패드" },
    { id: "mist", label: "미스트" },
  ],
  "에센스·세럼": [
    { id: "watery", label: "워터리" },
    { id: "oil", label: "오일" },
    { id: "viscous", label: "점성 액체" },
  ],
  "로션·크림": [
    { id: "lotion", label: "로션" },
    { id: "cream", label: "크림" },
    { id: "gel", label: "젤 크림" },
    { id: "balm", label: "밤" },
  ],
  "선케어": [
    { id: "cream", label: "선크림" },
    { id: "stick", label: "선스틱" },
    { id: "spray", label: "선스프레이" },
  ],
  "기타": [
    { id: "sheet", label: "마스크팩" },
    { id: "patch", label: "패치" },
    { id: "massage", label: "마사지 크림" },
  ],

  // 뷰티
  "베이스 메이크업": [
    { "id": "foundation", "label": "파운데이션" },
    { "id": "cushion", "label": "쿠션" },
    { "id": "bb_cc", "label": "BB·CC 크림" },
    { "id": "stick_foundation", "label": "스틱 파운데이션" }
  ],
  "컨실링": [
    { "id": "liquid_concealer", "label": "리퀴드 컨실러" },
    { "id": "stick_concealer", "label": "스틱 컨실러" },
    { "id": "pot_concealer", "label": "팟·크림 컨실러" }
  ],
  "아이브로우": [
    { "id": "pencil", "label": "아이브로우 펜슬" },
    { "id": "powder", "label": "아이브로우 파우더" },
    { "id": "brow_cara", "label": "브로우 카라" }
  ],
  "립": [
    { "id": "lip_balm", "label": "발색 립밤" },
    { "id": "tint", "label": "틴트" },
    { "id": "lip_stick", "label": "립스틱" }
  ],
  "쉐딩·하이라이터": [
    { "id": "powder_shading", "label": "쉐딩 파우더" },
    { "id": "stick_shading", "label": "쉐딩 스틱" },
    { "id": "highlighter", "label": "하이라이터" }
  ]
}

// Image Imports
import cleansingImg from "../assets/images/formcleansing.png"
import tonerImg from "../assets/images/skin.png"
import serumImg from "../assets/images/serum.png"
import lotionImg from "../assets/images/lotion.png"
import suncareImg from "../assets/images/sumcream.png"
import maskImg from "../assets/images/mask.png"
import foundationImg from "../assets/images/foundation.png"
import concealerImg from "../assets/images/concealer.png"
import eyebrowImg from "../assets/images/eyebrow.png"
import lipbalmImg from "../assets/images/lipbalm.png"
import shaddingImg from "../assets/images/shadding.png"


export const PRODUCT_CATEGORIES: Record<string, { id: string; label: string; image?: string }[]> = {
  skincare: [
    { id: "클렌징", label: "클렌징", image: cleansingImg },
    { id: "토너·스킨", label: "토너·스킨", image: tonerImg },
    { id: "에센스·세럼", label: "에센스·세럼", image: serumImg },
    { id: "로션·크림", label: "로션·크림", image: lotionImg },
    { id: "선케어", label: "선케어", image: suncareImg },
    { id: "기타", label: "기타", image: maskImg },
  ],
  beauty: [
    { id: "베이스 메이크업", label: "베이스 메이크업", image: foundationImg },
    { id: "컨실링", label: "컨실링", image: concealerImg },
    { id: "아이브로우", label: "아이브로우", image: eyebrowImg },
    { id: "립", label: "립", image: lipbalmImg },
    { id: "쉐딩·하이라이터", label: "쉐딩·하이라이터", image: shaddingImg },
  ],
}

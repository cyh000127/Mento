export const PRODUCT_SUBTYPES: Record<string, { id: string; label: string }[]> = {
  // 스킨케어
  클렌징: [
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
  선케어: [
    { id: "cream", label: "선크림" },
    { id: "stick", label: "선스틱" },
    { id: "spray", label: "선스프레이" },
  ],
  기타: [
    { id: "sheet", label: "마스크팩" },
    { id: "patch", label: "패치" },
    { id: "massage", label: "마사지 크림" },
  ],

  // 뷰티
  "베이스 메이크업": [
    { id: "foundation", label: "파운데이션" },
    { id: "cushion", label: "쿠션" },
    { id: "bb_cc", label: "BB·CC 크림" },
    { id: "stick", label: "스틱 파운데이션" },
  ],
  컨실링: [
    { id: "liquid_concealer", label: "리퀴드 컨실러" },
    { id: "stick_concealer", label: "스틱 컨실러" },
    { id: "pot_concealer", label: "팟·크림 컨실러" },
  ],
  아이브로우: [
    { id: "pencil", label: "아이브로우 펜슬" },
    { id: "powder", label: "아이브로우 파우더" },
    { id: "brow_cara", label: "브로우 카라" },
  ],
  립: [
    { id: "lip_balm", label: "발색 립밤" },
    { id: "tint", label: "틴트" },
    { id: "lip_stick", label: "립스틱" },
  ],
  "쉐딩·하이라이터": [
    { id: "powder_shading", label: "쉐딩 파우더" },
    { id: "stick_shading", label: "쉐딩 스틱" },
    { id: "highlighter", label: "하이라이터" },
  ],
};

// Image Imports
import cleansingImg from "../assets/images/formcleansing.png";
import tonerImg from "../assets/images/skin.png";
import serumImg from "../assets/images/serum.png";
import lotionImg from "../assets/images/lotion.png";
import suncareImg from "../assets/images/sumcream.png";
import maskImg from "../assets/images/mask.png";
import foundationImg from "../assets/images/foundation.png";
import concealerImg from "../assets/images/concealer.png";
import eyebrowImg from "../assets/images/eyebrow.png";
import lipbalmImg from "../assets/images/lipbalm.png";
import shaddingImg from "../assets/images/shadding.png";

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
};

import { api } from "./axios";

type SkinAnalysisRequest = {
  front_url: string;
  l30_url: string;
  r30_url: string;
  birth_date: string;
  gender: "male" | "female";
};

export const requestSkinAnalysis = async (payload: SkinAnalysisRequest) => {
  const { front_url, l30_url, r30_url } = payload;
  if (!front_url || !l30_url || !r30_url) {
    throw new Error("이미지 URL이 누락되었습니다.");
  }

  const response = await api.post("/skin-analysis", payload, {
    headers: { "Content-Type": "application/json" },
  });

  return response.data.data;
};

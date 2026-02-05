import { api } from "./axios";

type SkinAnalysisRequest = {
  front_url: string;
  l30_url: string;
  r30_url: string;
  birth_date: string;
  gender: "male" | "female";
};

type SkinAnalysisListParams = {
  page?: number;
  size?: number;
  sort?: string[];
};

type SkinAnalysisListResponse = {
  content: {
    id: number;
    created_at: string;
    total_score: number;
    skin_type_summary: string;
  }[];
  hasNext: boolean;
  totalPages: number;
  totalElements: number;
  page: number;
  size: number;
  isFirst: boolean;
  isLast: boolean;
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

export const getSkinAnalysisHistory = async (params: SkinAnalysisListParams = {}) => {
  const response = await api.get<SkinAnalysisListResponse>("/skin-analysis", {
    params: {
      page: params.page ?? 0,
      size: params.size ?? 10,
      sort: params.sort ?? ["createdAt,DESC"],
    },
  });

  return response.data;
};

type SkinAnalysisDetail = {
  score: number;
  grade: number;
  raw_value: number;
  description: string;
};

type SkinAnalysisDetailResponse = {
  total_score: number;
  total_grade: number;
  skin_type_summary: string;
  details: Record<string, SkinAnalysisDetail>;
};

export const getSkinAnalysisDetail = async (id: number) => {
  const response = await api.get<{ success: boolean; data: SkinAnalysisDetailResponse }>(`/skin-analysis/${id}`);
  return response.data.data;
};

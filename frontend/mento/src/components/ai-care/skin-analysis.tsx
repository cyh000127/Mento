import { useState, useRef } from "react";
import { Upload, Sparkles, X, Loader2, Droplets, Search, Minus, Sun, Dumbbell } from "lucide-react";
import { api } from "@/api/axios";
import { userApi } from "@/api/userApi";
import { requestSkinAnalysis } from "../../api/skinAnalysisApi";
import { AlertModal } from "@/components/common/alert-modal";
import type { AlertModalType } from "@/components/common/alert-modal";

interface UploadedImage {
  file: File;
  preview: string;
}

type AnalysisState = "upload" | "loading" | "result";

type SkinAnalysisResult = {
  total_score: number;
  total_grade: number;
  skin_type_summary: string;
  details: Record<string, SkinAnalysisDetail>;
};

type SkinAnalysisDetail = {
  score: number;
  grade: number;
  raw_value: number;
  description: string;
};

const METRIC_STYLE_MAP = {
  moisture: {
    label: "수분",
    icon: Droplets,
    color: "text-primary-500",
    bgColor: "bg-[#beeffc]",
    cardGradient: "from-[#beeffc] to-[#ccf8ff]",
    cardBg: "bg-[#ccf8ff]/50",
    textColor: "text-dark-bg",
  },
  pore: {
    label: "모공",
    icon: Search,
    color: "text-[#6fb896]",
    bgColor: "bg-[#bfeedd]",
    cardGradient: "from-[#bfeedd] to-[#dffaf0]",
    cardBg: "bg-[#dffaf0]/50",
    textColor: "text-dark-bg",
  },
  wrinkle: {
    label: "주름",
    icon: Minus,
    color: "text-[#9b93d4]",
    bgColor: "bg-[#e6e3fa]",
    cardGradient: "from-[#e6e3fa] to-[#f2f0ff]",
    cardBg: "bg-[#f2f0ff]/50",
    textColor: "text-dark-bg",
  },
  pigmentation: {
    label: "색소침착",
    icon: Sun,
    color: "text-primary-500",
    bgColor: "bg-[#ccf8ff]",
    cardGradient: "from-[#ccf8ff] to-[#beeffc]",
    cardBg: "bg-[#beeffc]/50",
    textColor: "text-dark-bg",
  },
  sagging: {
    label: "탄력",
    icon: Dumbbell,
    color: "text-[#6fb896]",
    bgColor: "bg-[#dffaf0]",
    cardGradient: "from-[#dffaf0] to-[#bfeedd]",
    cardBg: "bg-[#bfeedd]/50",
    textColor: "text-dark-bg",
  },
};

export function SkinAnalysis() {
  const [state, setState] = useState<AnalysisState>("upload");
  const [gender, setGender] = useState<string>("");
  const [birthDate, setBirthDate] = useState("");
  const [leftImage, setLeftImage] = useState<UploadedImage | null>(null);
  const [frontImage, setFrontImage] = useState<UploadedImage | null>(null);
  const [rightImage, setRightImage] = useState<UploadedImage | null>(null);
  const [loadingProgress, setLoadingProgress] = useState(0);
  const [analysisResult, setAnalysisResult] = useState<SkinAnalysisResult | null>(null);
  const [alertState, setAlertState] = useState({
    open: false,
    title: "알림",
    message: "",
    type: "info" as AlertModalType,
    confirmText: "확인",
  });

  const leftInputRef = useRef<HTMLInputElement>(null);
  const frontInputRef = useRef<HTMLInputElement>(null);
  const rightInputRef = useRef<HTMLInputElement>(null);

  const showAlert = (options: { title?: string; message: string; type?: AlertModalType; confirmText?: string }) => {
    setAlertState({
      open: true,
      title: options.title ?? "알림",
      message: options.message,
      type: options.type ?? "info",
      confirmText: options.confirmText ?? "확인",
    });
  };

  const getStatusTextClass = (status: string) => {
    if (status === "우수") return "text-green-700";
    if (status === "양호") return "text-primary-600";
    if (status === "보통") return "text-orange-600";
    if (status === "주의") return "text-red-600";
    return "text-text-secondary";
  };

  const getStatusFromGrade = (grade: number) => {
    if (grade === 1) return "우수";
    if (grade === 2) return "양호";
    if (grade === 3) return "보통";
    return "주의";
  };

  const getUiLevelFromGrade = (grade: number) => 6 - grade;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>, position: "left" | "front" | "right") => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith("image/")) {
      showAlert({
        title: "파일 형식 오류",
        message: "이미지 파일만 업로드 가능합니다.",
        type: "warning",
      });
      return;
    }

    // Validate file size (10MB)
    if (file.size > 10 * 1024 * 1024) {
      showAlert({
        title: "파일 크기 초과",
        message: "파일 크기는 10MB 이하여야 합니다.",
        type: "warning",
      });
      return;
    }

    const preview = URL.createObjectURL(file);
    const uploadedImage = { file, preview };

    switch (position) {
      case "left":
        setLeftImage(uploadedImage);
        break;
      case "front":
        setFrontImage(uploadedImage);
        break;
      case "right":
        setRightImage(uploadedImage);
        break;
    }
  };

  const handleRemoveImage = (position: "left" | "front" | "right") => {
    switch (position) {
      case "left":
        if (leftImage) URL.revokeObjectURL(leftImage.preview);
        setLeftImage(null);
        if (leftInputRef.current) leftInputRef.current.value = "";
        break;
      case "front":
        if (frontImage) URL.revokeObjectURL(frontImage.preview);
        setFrontImage(null);
        if (frontInputRef.current) frontInputRef.current.value = "";
        break;
      case "right":
        if (rightImage) URL.revokeObjectURL(rightImage.preview);
        setRightImage(null);
        if (rightInputRef.current) rightInputRef.current.value = "";
        break;
    }
  };

  const handleStartAnalysis = async () => {
    if (!gender) {
      showAlert({
        title: "필수 입력",
        message: "성별을 선택해주세요.",
        type: "warning",
      });
      return;
    }

    if (!birthDate) {
      showAlert({
        title: "필수 입력",
        message: "생년월일을 입력해주세요.",
        type: "warning",
      });
      return;
    }

    if (!leftImage || !frontImage || !rightImage) {
      showAlert({
        title: "필수 입력",
        message: "모든 사진을 업로드해주세요.",
        type: "warning",
      });
      return;
    }

    // 로딩 상태로 변경
    setState("loading");
    setLoadingProgress(0);
    setAnalysisResult(null);

    // 로딩 화면으로 전환 시 스크롤
    setTimeout(() => {
      const analysisSection = document.getElementById("skin-analysis");
      if (analysisSection) {
        analysisSection.scrollIntoView({ behavior: "smooth", block: "start" });
      }
    }, 100);

    // 프로그레스 바 애니메이션
    const progressInterval = setInterval(() => {
      setLoadingProgress((prev) => {
        if (prev >= 95) {
          clearInterval(progressInterval);
          return 95;
        }
        return prev + 5;
      });
    }, 150);

    try {
      const uploadFile = async (file: File) => {
        const formData = new FormData();
        formData.append("file", file);
        const response = await api.post("/files", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
        const fileUrl = response?.data?.data?.fileUrl;
        if (!fileUrl) {
          throw new Error("파일 업로드에 실패했습니다.");
        }
        return fileUrl as string;
      };

      // 1. 생년월일 정보 업데이트 API 호출
      await userApi.updateUserProfile({
        birthDate: birthDate,
      });

      const l30_url = await uploadFile(leftImage.file);
      const front_url = await uploadFile(frontImage.file);
      const r30_url = await uploadFile(rightImage.file);

      // 2. 피부 분석 API 호출 (TODO: 실제 피부 분석 API 구현)
      const analysisResponse = await requestSkinAnalysis({
        front_url,
        l30_url,
        r30_url,
        birth_date: birthDate,
        gender: gender as "male" | "female",
      });

      // 로딩 완료
      clearInterval(progressInterval);
      setLoadingProgress(100);

      // 결과 상태로 변경
      setAnalysisResult(analysisResponse);
      setState("result");

      // 결과 섹션으로 부드럽게 스크롤
      setTimeout(() => {
        const resultSection = document.getElementById("skin-analysis");
        if (resultSection) {
          resultSection.scrollIntoView({ behavior: "smooth", block: "start" });
        }
      }, 100);
    } catch (error) {
      console.error("분석 실패:", error);
      clearInterval(progressInterval);
      const errorMessage = error instanceof Error ? error.message : "분석 중 오류가 발생했습니다.";
      showAlert({
        title: "분석 실패",
        message: errorMessage,
        type: "error",
      });
      setState("upload");
      setLoadingProgress(0);
      setAnalysisResult(null);
    }
  };

  const totalScore = analysisResult?.total_score;
  const totalGrade = analysisResult?.total_grade;
  const totalStatus = totalGrade ? getStatusFromGrade(totalGrade) : "";
  const totalLevel = totalGrade ? getUiLevelFromGrade(totalGrade) : 0;
  const skinType = analysisResult?.skin_type_summary ?? "";
  const details = analysisResult?.details;
  const metricOrder: Array<keyof typeof METRIC_STYLE_MAP> = ["moisture", "pore", "wrinkle", "pigmentation", "sagging"];
  const orderedMetrics = metricOrder
    .map((key) => {
      const detail = details?.[key];
      if (!detail) return null;
      return { key, detail, style: METRIC_STYLE_MAP[key] };
    })
    .filter((item): item is { key: keyof typeof METRIC_STYLE_MAP; detail: SkinAnalysisDetail; style: (typeof METRIC_STYLE_MAP)[keyof typeof METRIC_STYLE_MAP] } => Boolean(item));
  const summaryItems = orderedMetrics.slice(0, 2).map((item) => ({
    label: item.style.label,
    status: getStatusFromGrade(item.detail.grade),
  }));

  return (
    <section id="skin-analysis" className="bg-background py-16 md:py-24">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Upload State */}
        {state === "upload" && (
          <div className="mx-auto max-w">
            {/* Left - Upload Area */}
            <h2 className="mb-4 text-2xl font-bold text-text-primary md:text-3xl">피부 분석 시작하기</h2>
            <p className="mb-8 text-text-secondary">사진을 업로드하면 AI가 피부 상태를 분석하여 맞춤 솔루션을 제안합니다.</p>

            {/* Gender and Birth Date Input */}
            <div className="mb-6 grid grid-cols-1 gap-4 md:grid-cols-2">
              {/* Gender Input - Left */}
              <div>
                <div className="mb-2 block text-sm font-medium text-text-primary">
                  성별 <span className="text-red-500">*</span>
                </div>
                <div className="flex gap-6 py-3">
                  <label className="w-1/2 flex cursor-pointer items-center gap-2">
                    <input
                      type="radio"
                      name="gender"
                      value="male"
                      checked={gender === "male"}
                      onChange={(e) => setGender(e.target.value)}
                      className="h-5 w-5 cursor-pointer border-2 border-border text-primary-500 focus:ring-2 focus:ring-primary-500/20 focus:ring-offset-0"
                    />
                    <span className="text-sm font-medium text-text-primary">남성</span>
                  </label>
                  <label className="w-1/2 flex cursor-pointer items-center gap-2">
                    <input
                      type="radio"
                      name="gender"
                      value="female"
                      checked={gender === "female"}
                      onChange={(e) => setGender(e.target.value)}
                      className="h-5 w-5 cursor-pointer border-2 border-border text-primary-500 focus:ring-2 focus:ring-primary-500/20 focus:ring-offset-0"
                    />
                    <span className="text-sm font-medium text-text-primary">여성</span>
                  </label>
                </div>
              </div>

              {/* Birth Date Input - Right */}
              <div>
                <label htmlFor="birth-date" className="mb-2 block text-sm font-medium text-text-primary">
                  생년월일 <span className="text-red-500">*</span>
                </label>
                <input
                  type="date"
                  id="birth-date"
                  value={birthDate}
                  onChange={(e) => setBirthDate(e.target.value)}
                  max={new Date().toISOString().split("T")[0]}
                  className="w-full rounded-lg border border-border bg-background px-4 py-3 text-text-primary focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
                  placeholder="YYYY-MM-DD"
                />
              </div>
            </div>

            <p className="mb-6 text-xs text-text-secondary">정확한 피부 분석을 위해 성별과 생년월일을 입력해주세요.</p>

            {/* Upload Boxes - 3 sections */}
            <div className="mb-6 grid grid-cols-1 gap-4 md:grid-cols-3">
              {/* Left Face Upload */}
              <div className="rounded-xl border-2 border-dashed border-primary-300 bg-primary-100/30 p-4 text-center transition-all hover:border-primary-400 hover:bg-primary-100/50">
                <input ref={leftInputRef} type="file" accept="image/*" onChange={(e) => handleFileChange(e, "left")} className="hidden" id="left-image-input" />
                {leftImage ? (
                  <div className="relative aspect-[3/4] w-full">
                    <img src={leftImage.preview} alt="좌측면" className="h-full w-full rounded-lg object-cover" />
                    <button
                      type="button"
                      onClick={() => handleRemoveImage("left")}
                      className="absolute right-2 top-2 flex h-6 w-6 items-center justify-center rounded-full bg-red-500 text-white hover:bg-red-600"
                    >
                      <X className="h-4 w-4" />
                    </button>
                  </div>
                ) : (
                  <div className="flex aspect-[3/4] w-full flex-col items-center justify-center">
                    <div className="mb-3 flex justify-center">
                      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary-500">
                        <Upload className="h-6 w-6 text-dark-bg" />
                      </div>
                    </div>
                    <p className="mb-2 text-sm font-medium text-text-primary">얼굴 좌측</p>
                    <button
                      type="button"
                      onClick={() => leftInputRef.current?.click()}
                      className="inline-flex items-center gap-2 rounded-lg bg-primary-500 px-3 py-1.5 text-xs font-medium text-dark-bg transition-colors hover:bg-primary-400"
                    >
                      <Upload className="h-3 w-3" />
                      파일 선택
                    </button>
                  </div>
                )}
              </div>

              {/* Front Face Upload */}
              <div className="rounded-xl border-2 border-dashed border-primary-300 bg-primary-100/30 p-4 text-center transition-all hover:border-primary-400 hover:bg-primary-100/50">
                <input ref={frontInputRef} type="file" accept="image/*" onChange={(e) => handleFileChange(e, "front")} className="hidden" id="front-image-input" />
                {frontImage ? (
                  <div className="relative aspect-[3/4] w-full">
                    <img src={frontImage.preview} alt="정면" className="h-full w-full rounded-lg object-cover" />
                    <button
                      type="button"
                      onClick={() => handleRemoveImage("front")}
                      className="absolute right-2 top-2 flex h-6 w-6 items-center justify-center rounded-full bg-red-500 text-white hover:bg-red-600"
                    >
                      <X className="h-4 w-4" />
                    </button>
                  </div>
                ) : (
                  <div className="flex aspect-[3/4] w-full flex-col items-center justify-center">
                    <div className="mb-3 flex justify-center">
                      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary-500">
                        <Upload className="h-6 w-6 text-dark-bg" />
                      </div>
                    </div>
                    <p className="mb-2 text-sm font-medium text-text-primary">얼굴 정면</p>
                    <button
                      type="button"
                      onClick={() => frontInputRef.current?.click()}
                      className="inline-flex items-center gap-2 rounded-lg bg-primary-500 px-3 py-1.5 text-xs font-medium text-dark-bg transition-colors hover:bg-primary-400"
                    >
                      <Upload className="h-3 w-3" />
                      파일 선택
                    </button>
                  </div>
                )}
              </div>

              {/* Right Face Upload */}
              <div className="rounded-xl border-2 border-dashed border-primary-300 bg-primary-100/30 p-4 text-center transition-all hover:border-primary-400 hover:bg-primary-100/50">
                <input ref={rightInputRef} type="file" accept="image/*" onChange={(e) => handleFileChange(e, "right")} className="hidden" id="right-image-input" />
                {rightImage ? (
                  <div className="relative aspect-[3/4] w-full">
                    <img src={rightImage.preview} alt="우측면" className="h-full w-full rounded-lg object-cover" />
                    <button
                      type="button"
                      onClick={() => handleRemoveImage("right")}
                      className="absolute right-2 top-2 flex h-6 w-6 items-center justify-center rounded-full bg-red-500 text-white hover:bg-red-600"
                    >
                      <X className="h-4 w-4" />
                    </button>
                  </div>
                ) : (
                  <div className="flex aspect-[3/4] w-full flex-col items-center justify-center">
                    <div className="mb-3 flex justify-center">
                      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary-500">
                        <Upload className="h-6 w-6 text-dark-bg" />
                      </div>
                    </div>
                    <p className="mb-2 text-sm font-medium text-text-primary">얼굴 우측</p>
                    <button
                      type="button"
                      onClick={() => rightInputRef.current?.click()}
                      className="inline-flex items-center gap-2 rounded-lg bg-primary-500 px-3 py-1.5 text-xs font-medium text-dark-bg transition-colors hover:bg-primary-400"
                    >
                      <Upload className="h-3 w-3" />
                      파일 선택
                    </button>
                  </div>
                )}
              </div>
            </div>

            {/* File Info */}
            <div className="mb-6 rounded-lg bg-muted/30 p-3 text-center">
              <p className="text-xs text-text-secondary">JPG, PNG 파일 (최대 10MB) | 모든 사진을 업로드해야 분석이 가능합니다</p>
            </div>

            {/* Guidelines */}
            <div className="rounded-xl bg-muted/50 p-4">
              <p className="mb-2 text-sm font-medium text-text-primary">정확한 분석을 위한 팁</p>
              <ul className="space-y-1 text-sm text-text-secondary">
                <li>- 자연광 아래에서 촬영해주세요</li>
                <li>- 메이크업 없는 맨 얼굴이 좋습니다</li>
                <li>- 좌측, 정면, 우측 사진을 모두 업로드해주세요</li>
              </ul>
            </div>

            {/* Analysis Button */}
            <button
              type="button"
              onClick={handleStartAnalysis}
              disabled={!gender || !birthDate || !leftImage || !frontImage || !rightImage}
              className="mt-6 w-full rounded-lg bg-primary-500 px-6 py-3 font-medium text-dark-bg transition-colors hover:bg-primary-400 disabled:cursor-not-allowed disabled:bg-muted disabled:text-text-secondary"
            >
              {gender && birthDate && leftImage && frontImage && rightImage ? (
                <>
                  <Sparkles className="mr-2 inline-block h-4 w-4" />
                  AI 피부 분석 시작
                </>
              ) : !gender ? (
                "성별을 선택해주세요"
              ) : !birthDate ? (
                "생년월일을 입력해주세요"
              ) : (
                "사진을 모두 업로드해주세요"
              )}
            </button>
          </div>
        )}

        {/* Loading State */}
        {state === "loading" && (
          <div className="mx-auto flex min-h-[60vh] max-w-2xl flex-col items-center justify-center text-center">
            <div className="mb-8 flex h-24 w-24 items-center justify-center rounded-full bg-primary-100">
              <Loader2 className="h-12 w-12 animate-spin text-primary-500" />
            </div>
            <h2 className="mb-4 text-2xl font-bold text-text-primary md:text-3xl">AI가 피부를 분석하고 있습니다</h2>
            <p className="mb-8 text-text-secondary">잠시만 기다려주세요. 곧 맞춤 분석 결과를 확인하실 수 있습니다.</p>
            <div className="w-full max-w-md">
              <div className="mb-2 h-2 overflow-hidden rounded-full bg-muted">
                <div className="h-full rounded-full bg-gradient-to-r from-primary-400 to-primary-500 transition-all duration-300 ease-out" style={{ width: `${loadingProgress}%` }} />
              </div>
              <p className="text-sm text-text-secondary">분석 진행중... {loadingProgress}%</p>
            </div>
          </div>
        )}

        {/* Result State */}
        {state === "result" && analysisResult && orderedMetrics.length > 0 && (
          <div className="mx-auto max-w-6xl">
            {/* Header */}
            <div className="mb-8 text-center">
              <h2 className="mb-2 text-4xl font-bold text-text-primary md:text-4xl">피부 분석 결과</h2>
              <p className="text-text-secondary">AI가 분석한 당신의 피부 상태를 확인해보세요</p>
            </div>

            {/* Top Summary Section */}
            <div className="mb-8 grid gap-6 md:grid-cols-2">
              {/* Total Score Card */}
              <div className="rounded-3xl bg-gradient-to-br from-primary-400 to-primary-500 p-8 text-center shadow-xl">
                <p className="mb-3 text-md font-medium text-white/90">피부 종합 점수</p>
                <div className="mb-4 flex items-center justify-center gap-3">
                  <p className="text-7xl font-bold text-white">{totalScore ?? ""}</p>
                  <div className="flex flex-col items-start">
                    <p className="text-2xl font-semibold text-white">점</p>
                    <div className="flex gap-1">
                      {[1, 2, 3, 4, 5].map((star) => (
                        <div key={star} className={`h-4 w-4 rounded-full ${totalLevel && star <= totalLevel ? "bg-white" : "bg-white/40"}`} />
                      ))}
                    </div>
                  </div>
                </div>
                <p className="text-md text-white/90">{totalGrade && totalStatus ? `5단계 중 ${totalGrade}단계 - ${totalStatus}` : ""}</p>
              </div>

              {/* Skin Type Summary Card */}
              <div className="rounded-3xl border-2 border-primary-300 bg-gradient-to-br from-primary-50 to-white p-8 shadow-lg">
                <p className="mb-3 text-md font-medium text-text-secondary">피부 타입</p>
                <p className="mb-4 text-4xl font-bold text-primary-600">{skinType ?? ""}</p>
                <div className="space-y-2">
                  {summaryItems.map((item, idx) => (
                    <div key={`${item.label}-${idx}`} className="flex items-center justify-between rounded-lg bg-white/60 px-3 py-2">
                      <span className="text-md font-medium text-text-primary">{item.label}</span>
                      <span className={`text-md font-semibold ${getStatusTextClass(item.status)}`}>{item.status}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Central Visualization Section */}
            <div className="mb-8 rounded-3xl border border-border bg-gradient-to-br from-primary-50/50 to-white p-8 shadow-lg">
              <h3 className="mb-6 text-center text-2xl font-bold text-text-primary">피부 상태 상세 분석</h3>

              <div className="mx-auto flex max-w-5xl flex-col items-center gap-6 lg:flex-row lg:items-center">
                {/* Radar Chart */}
                <div className="relative flex w-full max-w-lg items-center justify-center lg:w-1/2">
                  <svg viewBox="0 0 500 500" className="h-full w-full" style={{ maxHeight: "500px" }}>
                    {/* Background circles */}
                    {[180, 144, 108, 72, 36].map((radius, idx) => (
                      <circle key={`circle-${idx}`} cx="250" cy="250" r={radius} fill="none" stroke={idx === 0 ? "#b8f6ff" : "#e5e7eb"} strokeWidth="2" />
                    ))}

                    {/* Guide lines */}
                    {[0, 72, 144, 216, 288].map((angle, idx) => {
                      const radian = ((angle - 90) * Math.PI) / 180;
                      const x = 250 + 180 * Math.cos(radian);
                      const y = 250 + 180 * Math.sin(radian);
                      return <line key={`line-${idx}`} x1="250" y1="250" x2={x} y2={y} stroke="#d1d5db" strokeWidth="2" />;
                    })}

                    {/* Data polygon */}
                    <polygon
                      points={orderedMetrics
                        .map(({ detail }, idx) => {
                          const angle = [0, 72, 144, 216, 288][idx];
                          const radian = ((angle - 90) * Math.PI) / 180;
                          // 점수가 낮을수록 바깥쪽 (100 - value로 반전, 그리고 1.8배 스케일)
                          const radius = (100 - detail.score) * 1.8;
                          const x = 250 + radius * Math.cos(radian);
                          const y = 250 + radius * Math.sin(radian);
                          return `${x},${y}`;
                        })
                        .join(" ")}
                      fill="rgba(134, 239, 172, 0.3)"
                      stroke="#86efac"
                      strokeWidth="4"
                    />

                    {/* Data points - circles removed, icons will be overlaid */}
                    {orderedMetrics.map(({ style }, idx) => {
                      const angle = [0, 72, 144, 216, 288][idx];
                      const radian = ((angle - 90) * Math.PI) / 180;
                      const labelX = 250 + 210 * Math.cos(radian);
                      const labelY = 250 + 210 * Math.sin(radian);

                      return (
                        <g key={`point-${idx}`}>
                          <text x={labelX} y={labelY} textAnchor="middle" dominantBaseline="middle" className="text-xl font-bold" fill="#111827">
                            {style.label}
                          </text>
                        </g>
                      );
                    })}
                  </svg>

                  {/* Data point icons overlay */}
                  <div className="absolute inset-0" style={{ pointerEvents: "none" }}>
                    {orderedMetrics.map(({ detail, style }, idx) => {
                      const angle = [0, 72, 144, 216, 288][idx];
                      const radian = ((angle - 90) * Math.PI) / 180;
                      const radius = (100 - detail.score) * 1.8;
                      // SVG viewBox는 500x500이고, 중심은 250,250
                      // 실제 위치 계산: 중심(250) + radius * cos/sin
                      const svgX = 250 + radius * Math.cos(radian);
                      const svgY = 250 + radius * Math.sin(radian);
                      // SVG 좌표를 퍼센트로 변환 (viewBox 500 기준)
                      const percentX = (svgX / 500) * 100;
                      const percentY = (svgY / 500) * 100;

                      return (
                        <div
                          key={idx}
                          className={`absolute flex h-7 w-7 items-center justify-center rounded-full ${style.bgColor} border-2 border-white shadow-md`}
                          style={{
                            left: `${percentX}%`,
                            top: `${percentY}%`,
                            transform: `translate(-50%, -50%)`,
                          }}
                        >
                          <style.icon className={`h-4 w-4 ${style.color}`} />
                        </div>
                      );
                    })}
                  </div>
                </div>

                {/* Legend / Quick Stats */}
                <div className="w-full space-y-3 lg:w-1/2">
                  {orderedMetrics.map(({ detail, style }, idx) => {
                    const status = getStatusFromGrade(detail.grade);
                    return (
                      <div key={idx} className="flex items-center justify-between rounded-xl border border-border bg-white p-4 shadow-sm transition-all hover:shadow-md">
                        <div className="flex items-center gap-3">
                          <div className={`flex h-8 w-8 items-center justify-center rounded-full ${style.bgColor}`}>
                            <style.icon className={`h-4 w-4 ${style.color}`} />
                          </div>
                          <span className="font-medium text-text-primary">{style.label}</span>
                        </div>
                        <div className="flex items-center gap-3">
                          <span className="text-md font-semibold text-text-secondary">{detail.score}점</span>
                          <span
                            className={`rounded-full px-3 py-1 text-xs font-medium ${
                              status === "우수"
                                ? "bg-green-100 text-green-700 font-semibold"
                                : status === "양호"
                                ? "bg-primary-100 text-primary-500 font-semibold"
                                : status === "보통"
                                ? "bg-orange-100 text-orange-600 font-semibold"
                                : status === "주의"
                                ? "bg-red-100 text-red-600 font-semibold"
                                : "bg-muted text-text-secondary"
                            }`}
                          >
                            {status}
                          </span>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>

            {/* Detail Cards Section */}
            <div className="mb-8 grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {orderedMetrics.map(({ detail, style }, idx) => {
                return (
                  <div key={idx} className={`overflow-hidden rounded-2xl border border-border ${style.cardBg} shadow-md transition-all hover:shadow-lg`}>
                    <div className={`bg-gradient-to-r ${style.cardGradient} px-6 py-4 ${style.textColor}`}>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <style.icon className={`h-6 w-6 ${style.color}`} />
                          <h4 className="text-xl font-bold">{style.label}</h4>
                        </div>
                        <div className="text-right">
                          <p className="text-3xl font-bold">{detail.score}</p>
                          <p className="text-xs opacity-90">점</p>
                        </div>
                      </div>
                    </div>
                    <div className="p-6">
                      <div className="mb-3 flex items-center gap-1">
                        {[...Array(5)].map((_, i) => (
                          <div key={i} className={`h-2 w-2 rounded-full ${i < getUiLevelFromGrade(detail.grade) ? "bg-[#22c55e]" : "bg-gray-300"}`} />
                        ))}
                        <span className="ml-2 text-sm text-text-secondary">{detail.grade}/5 단계</span>
                      </div>
                      <p className="text-sm leading-relaxed text-text-secondary">{detail.description}</p>
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Retry Button */}
            <div className="mt-8 text-center">
              <button
                type="button"
                onClick={() => {
                  setState("upload");
                  setGender("");
                  setBirthDate("");
                  setLeftImage(null);
                  setFrontImage(null);
                  setRightImage(null);
                }}
                className="inline-flex items-center gap-2 rounded-xl bg-primary-500 px-8 py-4 font-medium text-dark-bg shadow-lg transition-all hover:bg-primary-400 hover:shadow-xl hover:scale-105"
              >
                <Sparkles className="h-5 w-5" />
                다시 분석하기
              </button>
            </div>
          </div>
        )}
        <AlertModal
          open={alertState.open}
          onOpenChange={(open) => setAlertState((prev) => ({ ...prev, open }))}
          title={alertState.title}
          message={alertState.message}
          type={alertState.type}
          confirmText={alertState.confirmText}
        />
      </div>
    </section>
  );
}

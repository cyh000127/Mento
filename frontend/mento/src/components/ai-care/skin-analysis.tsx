import { useState, useRef, useEffect } from "react";
import { Upload, Sparkles, X, Loader2 } from "lucide-react";
import { api } from "@/api/axios";
import { userApi } from "@/api/userApi";
import { requestSkinAnalysis } from "../../api/skinAnalysisApi";
import { SkinAnalysisResult } from "./skin-analysis-result";
import { AlertModal } from "@/components/common/alert-modal";
import type { AlertModalType } from "@/components/common/alert-modal";
import type { SkinAnalysisDetailData } from "@/types/ai-skincare";

interface UploadedImage {
  file: File;
  preview: string;
}

type AnalysisState = "upload" | "loading" | "result";

interface SkinAnalysisProps {
  onResultStateChange?: (showHero: boolean) => void;
}

export function SkinAnalysis({ onResultStateChange }: SkinAnalysisProps) {
  const [state, setState] = useState<AnalysisState>("upload");
  const [gender, setGender] = useState<string>("");
  const [birthDate, setBirthDate] = useState("");
  const [leftImage, setLeftImage] = useState<UploadedImage | null>(null);
  const [frontImage, setFrontImage] = useState<UploadedImage | null>(null);
  const [rightImage, setRightImage] = useState<UploadedImage | null>(null);
  const [loadingProgress, setLoadingProgress] = useState(0);
  const [analysisResult, setAnalysisResult] = useState<SkinAnalysisDetailData | null>(null);
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

  // Notify parent component when state changes
  useEffect(() => {
    if (onResultStateChange) {
      // Hide hero when showing results, show hero when in upload or loading state
      onResultStateChange(state !== "result");
    }
  }, [state, onResultStateChange]);

  const showAlert = (options: { title?: string; message: string; type?: AlertModalType; confirmText?: string }) => {
    setAlertState({
      open: true,
      title: options.title ?? "알림",
      message: options.message,
      type: options.type ?? "info",
      confirmText: options.confirmText ?? "확인",
    });
  };



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

  const handleRetry = () => {
    setState("upload");
    setGender("");
    setBirthDate("");
    setLeftImage(null);
    setFrontImage(null);
    setRightImage(null);
  };

  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  const maxDate = yesterday.toLocaleDateString("sv-SE"); // YYYY-MM-DD

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
                  max={maxDate}
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
                <li>- 정면 사진 1장과 얼굴을 좌측 30도, 우측 30도 돌린 사진을 각각 촬영해 업로드해주세요</li>
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
        {state === "result" && analysisResult && <SkinAnalysisResult analysisResult={analysisResult} onRetry={handleRetry} showRetryButton={true} />}
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

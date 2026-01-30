import { useState, useRef } from "react";
import { Upload, Sparkles, ChevronRight, X, Loader2 } from "lucide-react";
import { userApi } from "@/api/user";

interface UploadedImage {
  file: File;
  preview: string;
}

type AnalysisState = "upload" | "loading" | "result";

export function SkinAnalysis() {
  const [state, setState] = useState<AnalysisState>("upload");
  const [gender, setGender] = useState<string>("");
  const [birthDate, setBirthDate] = useState("");
  const [leftImage, setLeftImage] = useState<UploadedImage | null>(null);
  const [frontImage, setFrontImage] = useState<UploadedImage | null>(null);
  const [rightImage, setRightImage] = useState<UploadedImage | null>(null);

  const leftInputRef = useRef<HTMLInputElement>(null);
  const frontInputRef = useRef<HTMLInputElement>(null);
  const rightInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>, position: "left" | "front" | "right") => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith("image/")) {
      alert("이미지 파일만 업로드 가능합니다.");
      return;
    }

    // Validate file size (10MB)
    if (file.size > 10 * 1024 * 1024) {
      alert("파일 크기는 10MB 이하여야 합니다.");
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
      alert("성별을 선택해주세요.");
      return;
    }

    if (!birthDate) {
      alert("생년월일을 입력해주세요.");
      return;
    }

    if (!leftImage || !frontImage || !rightImage) {
      alert("모든 사진을 업로드해주세요.");
      return;
    }

    // 로딩 상태로 변경
    setState("loading");

    try {
      // 1. 생년월일 정보 업데이트 API 호출
      console.log("생년월일 업데이트 중:", birthDate);
      await userApi.updateUserProfile({
        birthDate: birthDate,
      });
      console.log("생년월일 업데이트 완료");

      // 2. 피부 분석 API 호출 (TODO: 실제 피부 분석 API 구현)
      

      // 시뮬레이션: 3초 후 결과 표시
      await new Promise((resolve) => setTimeout(resolve, 3000));

      // 결과 상태로 변경
      setState("result");
    } catch (error) {
      console.error("분석 실패:", error);
      const errorMessage = error instanceof Error ? error.message : "분석 중 오류가 발생했습니다.";
      alert(errorMessage);
      setState("upload");
    }
  };

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
                <div className="h-full animate-pulse rounded-full bg-gradient-to-r from-primary-400 to-primary-500" style={{ width: "70%" }} />
              </div>
              <p className="text-sm text-text-secondary">분석 진행중...</p>
            </div>
          </div>
        )}

        {/* Result State */}
        {state === "result" && (
          <div className="mx-auto max-w">
            <div className="mb-8 text-center">
              <h2 className="mb-4 text-2xl font-bold text-text-primary md:text-3xl">피부 분석 결과</h2>
              <p className="text-text-secondary">AI가 분석한 당신의 피부 상태입니다</p>
            </div>

            {/* Score Header */}
            <div className="mb-8 rounded-2xl border border-border bg-background p-8 shadow-lg">
              <div className="mb-6 flex items-center justify-between">
                <div>
                  <p className="mb-2 text-sm text-text-secondary">피부 종합 점수</p>
                  <p className="text-5xl font-bold text-text-primary">87점</p>
                </div>
                <div className="flex h-20 w-20 items-center justify-center rounded-full bg-gradient-to-br from-primary-400 to-primary-500">
                  <Sparkles className="h-10 w-10 text-dark-bg" />
                </div>
              </div>

              {/* Analysis Results */}
              <div className="space-y-4">
                <div>
                  <div className="mb-2 flex items-center justify-between text-sm">
                    <span className="font-medium text-text-primary">수분도</span>
                    <span className="font-semibold text-text-primary">92%</span>
                  </div>
                  <div className="h-3 overflow-hidden rounded-full bg-muted">
                    <div className="h-full w-[92%] rounded-full bg-gradient-to-r from-primary-400 to-primary-500" />
                  </div>
                </div>
                <div>
                  <div className="mb-2 flex items-center justify-between text-sm">
                    <span className="font-medium text-text-primary">유분 밸런스</span>
                    <span className="font-semibold text-text-primary">78%</span>
                  </div>
                  <div className="h-3 overflow-hidden rounded-full bg-muted">
                    <div className="h-full w-[78%] rounded-full bg-gradient-to-r from-pastel-green-200 to-pastel-green-100" />
                  </div>
                </div>
                <div>
                  <div className="mb-2 flex items-center justify-between text-sm">
                    <span className="font-medium text-text-primary">모공 상태</span>
                    <span className="font-semibold text-text-primary">85%</span>
                  </div>
                  <div className="h-3 overflow-hidden rounded-full bg-muted">
                    <div className="h-full w-[85%] rounded-full bg-gradient-to-r from-pastel-purple-200 to-pastel-purple-100" />
                  </div>
                </div>
                <div>
                  <div className="mb-2 flex items-center justify-between text-sm">
                    <span className="font-medium text-text-primary">탄력도</span>
                    <span className="font-semibold text-text-primary">89%</span>
                  </div>
                  <div className="h-3 overflow-hidden rounded-full bg-muted">
                    <div className="h-full w-[89%] rounded-full bg-gradient-to-r from-pastel-blue-200 to-pastel-blue-100" />
                  </div>
                </div>
              </div>
            </div>

            {/* AI Recommendation */}
            <div className="rounded-2xl border border-border bg-primary-100/30 p-8 shadow-lg">
              <div className="mb-4 flex items-center gap-2">
                <Sparkles className="h-6 w-6 text-primary-500" />
                <h3 className="text-xl font-semibold text-text-primary">AI 맞춤 추천</h3>
              </div>
              <p className="mb-6 leading-relaxed text-text-secondary">
                수분도가 높은 편이지만, 유분 밸런스 개선이 필요합니다. 나이아신아마이드 성분의 세럼과 가벼운 젤 타입 보습제를 추천드립니다. 또한, 모공 관리를 위해 주 2-3회 AHA/BHA 토너 사용을
                권장합니다.
              </p>
              <button type="button" className="inline-flex items-center gap-2 rounded-lg bg-primary-500 px-6 py-3 font-medium text-dark-bg transition-colors hover:bg-primary-400">
                맞춤 제품 보기
                <ChevronRight className="h-5 w-5" />
              </button>
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
                className="text-sm text-text-secondary underline hover:text-text-primary"
              >
                다시 분석하기
              </button>
            </div>
          </div>
        )}
      </div>
    </section>
  );
}

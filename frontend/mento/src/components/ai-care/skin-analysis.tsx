import { useState, useRef } from "react";
import { Upload, Sparkles, ChevronRight, X } from "lucide-react";

interface UploadedImage {
  file: File;
  preview: string;
}

export function SkinAnalysis() {
  const [, setStep] = useState(1);
  const [analysisComplete, setAnalysisComplete] = useState(false);
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

  const handleStartAnalysis = () => {
    if (!birthDate) {
      alert("생년월일을 입력해주세요.");
      return;
    }

    if (!leftImage || !frontImage || !rightImage) {
      alert("모든 사진을 업로드해주세요.");
      return;
    }

    setStep(2);
    // Simulate analysis
    // API request will use birthDate in format: "YYYY-MM-DD"
    console.log("Birth Date:", birthDate);
    console.log("Images:", { leftImage, frontImage, rightImage });

    setTimeout(() => {
      setAnalysisComplete(true);
    }, 2000);
  };

  return (
    <section id="skin-analysis" className="bg-background py-16 md:py-24">
      <div className="mx-auto max-w-[1200px] px-6">
        <div className="grid items-start gap-12 lg:grid-cols-2">
          {/* Left - Upload Area */}
          <div>
            <h2 className="mb-4 text-2xl font-bold text-text-primary md:text-3xl">피부 분석 시작하기</h2>
            <p className="mb-8 text-text-secondary">사진을 업로드하면 AI가 피부 상태를 분석하여 맞춤 솔루션을 제안합니다.</p>

            {/* Birth Date Input */}
            <div className="mb-6">
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
              <p className="mt-6 text-xs text-text-secondary">정확한 피부 분석을 위해 생년월일을 입력해주세요.</p>
            </div>

            {/* Upload Boxes - 3 sections */}
            <div className="mb-6 grid grid-cols-1 gap-4 md:grid-cols-3">
              {/* Left Face Upload */}
              <div className="rounded-xl border-2 border-dashed border-primary-300 bg-primary-100/30 p-4 text-center transition-all hover:border-primary-400 hover:bg-primary-100/50">
                <input ref={leftInputRef} type="file" accept="image/*" onChange={(e) => handleFileChange(e, "left")} className="hidden" id="left-image-input" />
                {leftImage ? (
                  <div className="relative h-40 w-full">
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
                  <div className="flex h-40 w-full flex-col items-center justify-center">
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
                  <div className="relative h-40 w-full">
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
                  <div className="flex h-40 w-full flex-col items-center justify-center">
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
                  <div className="relative h-40 w-full">
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
                  <div className="flex h-40 w-full flex-col items-center justify-center">
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
              disabled={!birthDate || !leftImage || !frontImage || !rightImage}
              className="mt-6 w-full rounded-lg bg-primary-500 px-6 py-3 font-medium text-dark-bg transition-colors hover:bg-primary-400 disabled:cursor-not-allowed disabled:bg-muted disabled:text-text-secondary"
            >
              {birthDate && leftImage && frontImage && rightImage ? (
                <>
                  <Sparkles className="mr-2 inline-block h-4 w-4" />
                  AI 피부 분석 시작
                </>
              ) : !birthDate ? (
                "생년월일을 입력해주세요"
              ) : (
                "사진을 모두 업로드해주세요"
              )}
            </button>
          </div>

          {/* Right - Analysis Result Preview */}
          <div className="rounded-2xl border border-border bg-background p-6 shadow-lg">
            {!analysisComplete ? (
              <div className="flex h-full flex-col items-center justify-center py-12 text-center">
                <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-muted">
                  <Sparkles className="h-10 w-10 text-text-secondary" />
                </div>
                <h3 className="mb-2 text-lg font-semibold text-text-primary">분석 결과가 여기에 표시됩니다</h3>
                <p className="mb-6 text-sm text-text-secondary">사진을 업로드하면 AI가 피부 상태를 분석합니다</p>
                <button
                  type="button"
                  onClick={handleStartAnalysis}
                  className="inline-flex items-center gap-2 rounded-lg bg-primary-500 px-6 py-3 font-medium text-dark-bg transition-colors hover:bg-primary-400"
                >
                  <Sparkles className="h-4 w-4" />
                  샘플 분석 보기
                </button>
              </div>
            ) : (
              <div>
                {/* Score Header */}
                <div className="mb-6 flex items-center justify-between">
                  <div>
                    <p className="text-sm text-text-secondary">피부 종합 점수</p>
                    <p className="text-3xl font-bold text-text-primary">87점</p>
                  </div>
                  <div className="flex h-16 w-16 items-center justify-center rounded-full bg-gradient-to-br from-primary-400 to-primary-500">
                    <Sparkles className="h-8 w-8 text-dark-bg" />
                  </div>
                </div>

                {/* Analysis Results */}
                <div className="mb-6 space-y-4">
                  <div>
                    <div className="mb-1.5 flex items-center justify-between text-sm">
                      <span className="text-text-secondary">수분도</span>
                      <span className="font-medium text-text-primary">92%</span>
                    </div>
                    <div className="h-2.5 overflow-hidden rounded-full bg-muted">
                      <div className="h-full w-[92%] rounded-full bg-gradient-to-r from-primary-400 to-primary-500" />
                    </div>
                  </div>
                  <div>
                    <div className="mb-1.5 flex items-center justify-between text-sm">
                      <span className="text-text-secondary">유분 밸런스</span>
                      <span className="font-medium text-text-primary">78%</span>
                    </div>
                    <div className="h-2.5 overflow-hidden rounded-full bg-muted">
                      <div className="h-full w-[78%] rounded-full bg-gradient-to-r from-pastel-green-200 to-pastel-green-100" />
                    </div>
                  </div>
                  <div>
                    <div className="mb-1.5 flex items-center justify-between text-sm">
                      <span className="text-text-secondary">모공 상태</span>
                      <span className="font-medium text-text-primary">85%</span>
                    </div>
                    <div className="h-2.5 overflow-hidden rounded-full bg-muted">
                      <div className="h-full w-[85%] rounded-full bg-gradient-to-r from-pastel-purple-200 to-pastel-purple-100" />
                    </div>
                  </div>
                  <div>
                    <div className="mb-1.5 flex items-center justify-between text-sm">
                      <span className="text-text-secondary">탄력도</span>
                      <span className="font-medium text-text-primary">89%</span>
                    </div>
                    <div className="h-2.5 overflow-hidden rounded-full bg-muted">
                      <div className="h-full w-[89%] rounded-full bg-gradient-to-r from-pastel-blue-200 to-pastel-blue-100" />
                    </div>
                  </div>
                </div>

                {/* AI Recommendation */}
                <div className="rounded-xl bg-primary-100/50 p-4">
                  <div className="mb-2 flex items-center gap-2">
                    <Sparkles className="h-4 w-4 text-primary-500" />
                    <span className="text-sm font-medium text-text-primary">AI 추천</span>
                  </div>
                  <p className="mb-3 text-sm text-text-secondary">수분도가 높은 편이지만, 유분 밸런스 개선이 필요합니다. 나이아신아마이드 성분의 세럼과 가벼운 젤 타입 보습제를 추천드립니다.</p>
                  <button type="button" className="inline-flex items-center gap-1 text-sm font-medium text-primary-500 hover:text-primary-400">
                    맞춤 제품 보기
                    <ChevronRight className="h-4 w-4" />
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </section>
  );
}

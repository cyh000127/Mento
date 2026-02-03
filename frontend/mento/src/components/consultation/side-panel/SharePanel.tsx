import { useRef, useState, useEffect, type ChangeEvent } from "react";
import { uploadConsultationMedia } from "@/api/consultationMediaApi";
import {
  ALLOWED_IMAGE_EXTENSIONS,
  ALLOWED_VIDEO_EXTENSIONS,
  MAX_SINGLE_FILE_BYTES,
  MAX_TOTAL_BYTES,
  type SharedMediaFile,
} from "@/types/consultationMedia";

export interface SharePanelProps {
  reservationId: number | null;
  onShare: (files: Array<Pick<SharedMediaFile, "fileUrl" | "fileType">>) => void;
  incomingSharedFiles: SharedMediaFile[];
}

const getFileExtension = (name: string) => {
  const parts = name.split(".");
  if (parts.length < 2) return "";
  return parts[parts.length - 1].toLowerCase();
};

const resolveFileType = (file: File): SharedMediaFile["fileType"] | null => {
  const extension = getFileExtension(file.name);
  if (ALLOWED_IMAGE_EXTENSIONS.includes(extension as (typeof ALLOWED_IMAGE_EXTENSIONS)[number])) {
    return "IMAGE";
  }
  if (ALLOWED_VIDEO_EXTENSIONS.includes(extension as (typeof ALLOWED_VIDEO_EXTENSIONS)[number])) {
    return "VIDEO";
  }
  return null;
};

const normalizeUploadResponse = (data: unknown) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (typeof data === "object") {
    const record = data as Record<string, unknown>;
    if (Array.isArray(record.files)) return record.files as unknown[];
    if (Array.isArray(record.uploadedFiles)) return record.uploadedFiles as unknown[];
    if (typeof record.fileUrl === "string") return [record];
  }
  return [];
};

const mergeSharedItems = (current: SharedMediaFile[], incoming: SharedMediaFile[]) => {
  if (incoming.length === 0) return current;
  const seen = new Set(current.map((item) => item.fileUrl));
  const next = [...current];
  incoming.forEach((item) => {
    if (!seen.has(item.fileUrl)) {
      seen.add(item.fileUrl);
      next.push(item);
    }
  });
  return next;
};

export function SharePanel({ reservationId, onShare, incomingSharedFiles }: SharePanelProps) {
  const inputRef = useRef<HTMLInputElement | null>(null);
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [uploading, setUploading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [sharedItems, setSharedItems] = useState<SharedMediaFile[]>([]);

  useEffect(() => {
    setSharedItems((prev) => mergeSharedItems(prev, incomingSharedFiles));
  }, [incomingSharedFiles]);

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(event.target.files ?? []);
    setSelectedFiles(files);
    setErrorMessage(null);
  };

  const validateFiles = (files: File[]) => {
    if (files.length === 0) return "업로드할 파일을 선택해주세요.";
    const totalBytes = files.reduce((sum, file) => sum + file.size, 0);
    if (totalBytes > MAX_TOTAL_BYTES) {
      return "총 업로드 용량은 500MB를 초과할 수 없습니다.";
    }
    for (const file of files) {
      if (file.size > MAX_SINGLE_FILE_BYTES) {
        return `${file.name} 파일은 100MB를 초과할 수 없습니다.`;
      }
      if (!resolveFileType(file)) {
        return `${file.name} 파일 형식이 지원되지 않습니다.`;
      }
    }
    return null;
  };

  const handleUpload = async () => {
    if (!reservationId) {
      setErrorMessage("예약 ID가 없어 업로드할 수 없습니다.");
      return;
    }
    const validationError = validateFiles(selectedFiles);
    if (validationError) {
      setErrorMessage(validationError);
      return;
    }

    setUploading(true);
    setErrorMessage(null);

    try {
      const responseData = await uploadConsultationMedia(reservationId, selectedFiles);
      const normalizedData = responseData?.data ?? responseData;
      const items = normalizeUploadResponse(normalizedData);

      if (items.length === 0) {
        throw new Error("업로드 결과를 확인할 수 없습니다.");
      }

      const uploadedItems: SharedMediaFile[] = items.map((item, index) => {
        if (typeof item === "string") {
          const fileType = resolveFileType(selectedFiles[index]) ?? "IMAGE";
          return { fileUrl: item, fileType, name: selectedFiles[index]?.name, size: selectedFiles[index]?.size };
        }
        const record = item as { fileUrl?: string };
        const fileUrl = record.fileUrl ?? "";
        const fileType = resolveFileType(selectedFiles[index]) ?? "IMAGE";
        return { fileUrl, fileType, name: selectedFiles[index]?.name, size: selectedFiles[index]?.size };
      });

      const validItems = uploadedItems.filter((item) => item.fileUrl);
      if (validItems.length === 0) {
        throw new Error("업로드된 파일 URL이 없습니다.");
      }

      setSharedItems((prev) => mergeSharedItems(prev, validItems));
      onShare(validItems.map(({ fileUrl, fileType }) => ({ fileUrl, fileType })));
      setSelectedFiles([]);
      if (inputRef.current) {
        inputRef.current.value = "";
      }
    } catch (error) {
      console.error("미디어 업로드 실패:", error);
      setErrorMessage("미디어 업로드에 실패했습니다. 다시 시도해주세요.");
    } finally {
      setUploading(false);
    }
  };

  const handleBrowseClick = () => {
    inputRef.current?.click();
  };

  return (
    <div className="flex flex-col h-full">
      {/* 헤더 */}
      <div className="p-4 border-b border-gray-800">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-cyan-600/20 rounded-lg">
            <svg className="w-5 h-5 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"
              />
            </svg>
          </div>
          <div>
            <h3 className="text-base font-semibold text-gray-200">미디어 공유</h3>
            <p className="text-xs text-gray-500">이미지 또는 영상 공유</p>
          </div>
        </div>
      </div>

      {/* 업로드 영역 */}
      <div className="p-4 space-y-4">
        {/* 파일 선택 영역 */}
        <div className="relative">
          <input
            ref={inputRef}
            type="file"
            multiple
            accept=".jpg,.jpeg,.png,.webp,.mp4,.mov,.webm,.mkv"
            onChange={handleFileChange}
            className="hidden"
          />
          <div
            onClick={handleBrowseClick}
            className="border-2 border-dashed border-gray-700 rounded-lg p-6 cursor-pointer hover:border-cyan-500 hover:bg-gray-800/50 transition-all"
          >
            <div className="text-center">
              <svg
                className="mx-auto h-12 w-12 text-gray-600 mb-3"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
                />
              </svg>
              <p className="text-sm text-gray-400 mb-1">클릭하여 파일 선택</p>
              <p className="text-xs text-gray-600">JPG, PNG, WEBP, MP4, MOV, WEBM, MKV</p>
            </div>
          </div>
        </div>

        {/* 선택된 파일 정보 */}
        {selectedFiles.length > 0 && (
          <div className="bg-gray-800 rounded-lg p-3 space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-xs font-medium text-gray-400">선택된 파일</span>
              <span className="text-xs text-cyan-400">{selectedFiles.length}개</span>
            </div>
            <div className="space-y-1 max-h-24 overflow-y-auto">
              {selectedFiles.map((file, idx) => (
                <div key={idx} className="flex items-center gap-2 text-xs">
                  <svg className="w-4 h-4 text-gray-500 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path
                      fillRule="evenodd"
                      d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z"
                      clipRule="evenodd"
                    />
                  </svg>
                  <span className="text-gray-400 truncate flex-1">{file.name}</span>
                  <span className="text-gray-600">{(file.size / 1024 / 1024).toFixed(1)}MB</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 업로드 버튼 */}
        <button
          onClick={handleUpload}
          disabled={uploading || selectedFiles.length === 0}
          className="w-full px-4 py-3 rounded-lg bg-gradient-to-r from-cyan-600 to-blue-600 text-white text-sm font-medium disabled:from-gray-700 disabled:to-gray-700 disabled:cursor-not-allowed hover:from-cyan-500 hover:to-blue-500 transition-all shadow-lg disabled:shadow-none"
        >
          {uploading ? (
            <span className="flex items-center justify-center gap-2">
              <svg className="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                />
              </svg>
              업로드 중...
            </span>
          ) : (
            <span className="flex items-center justify-center gap-2">
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10"
                />
              </svg>
              업로드
            </span>
          )}
        </button>

        {/* 에러 메시지 */}
        {errorMessage && (
          <div className="flex items-start gap-2 p-3 bg-red-900/20 border border-red-800 rounded-lg">
            <svg className="w-5 h-5 text-red-400 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path
                fillRule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                clipRule="evenodd"
              />
            </svg>
            <p className="text-sm text-red-300">{errorMessage}</p>
          </div>
        )}
      </div>

      {/* 공유된 미디어 목록 */}
      {sharedItems.length > 0 && (
        <div className="flex-1 overflow-y-auto p-4 border-t border-gray-800">
          <h4 className="text-xs font-semibold text-gray-500 uppercase mb-3">공유된 미디어 ({sharedItems.length})</h4>
          <div className="grid grid-cols-2 gap-3">
            {sharedItems.map((item) =>
              item.fileType === "IMAGE" ? (
                <div key={item.fileUrl} className="group relative aspect-square rounded-lg overflow-hidden bg-gray-800 border border-gray-700 hover:border-cyan-500 transition-all">
                  <img src={item.fileUrl} alt={item.name ?? "shared"} className="w-full h-full object-cover" />
                  <div className="absolute inset-0 bg-black/0 group-hover:bg-black/40 transition-all flex items-center justify-center">
                    <svg
                      className="w-8 h-8 text-white opacity-0 group-hover:opacity-100 transition-opacity"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                      />
                    </svg>
                  </div>
                </div>
              ) : (
                <div key={item.fileUrl} className="group relative aspect-square rounded-lg overflow-hidden bg-gray-800 border border-gray-700 hover:border-cyan-500 transition-all">
                  <video src={item.fileUrl} className="w-full h-full object-cover" />
                  <div className="absolute inset-0 bg-black/60 flex items-center justify-center">
                    <svg className="w-10 h-10 text-white" fill="currentColor" viewBox="0 0 20 20">
                      <path
                        fillRule="evenodd"
                        d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z"
                        clipRule="evenodd"
                      />
                    </svg>
                  </div>
                </div>
              )
            )}
          </div>
        </div>
      )}
    </div>
  );
}

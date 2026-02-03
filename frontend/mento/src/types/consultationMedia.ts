export type MediaFileType = "IMAGE" | "VIDEO";

export interface SharedMediaFile {
  fileUrl: string;
  fileType: MediaFileType;
  name?: string;
  size?: number;
}

export const MAX_SINGLE_FILE_BYTES = 100 * 1024 * 1024;
export const MAX_TOTAL_BYTES = 500 * 1024 * 1024;
export const ALLOWED_IMAGE_EXTENSIONS = ["jpg", "jpeg", "png", "webp"] as const;
export const ALLOWED_VIDEO_EXTENSIONS = ["mp4", "mov", "webm", "mkv"] as const;

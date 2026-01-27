package com.mento.domain.file.validator;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mento.common.error.ErrorCode;
import com.mento.domain.file.exception.FileStorageException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileValidator {

	private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
		"image/jpeg",
		"image/jpg",
		"image/png",
		"image/gif",
		"image/webp"
	);

	private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
		"video/mp4",
		"video/mpeg",
		"video/webm"
	);

	public void validateFile(final MultipartFile file) {
		validateNotEmpty(file);
		validateContentType(file);
		validateFilename(file);
	}

	private void validateNotEmpty(final MultipartFile file) {
		if (file.isEmpty()) {
			throw new FileStorageException(ErrorCode.FILE_EMPTY);
		}
	}

	private void validateContentType(final MultipartFile file) {
		String contentType = file.getContentType();
		if (contentType == null || !isAllowedContentType(contentType)) {
			throw new FileStorageException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
		}
	}

	private boolean isAllowedContentType(final String contentType) {
		return ALLOWED_IMAGE_TYPES.contains(contentType) || ALLOWED_VIDEO_TYPES.contains(contentType);
	}

	private void validateFilename(final MultipartFile file) {
		String filename = file.getOriginalFilename();
		if (filename == null || filename.isBlank()
			|| filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new FileStorageException(ErrorCode.FILE_NAME_INVALID);
		}
	}
}

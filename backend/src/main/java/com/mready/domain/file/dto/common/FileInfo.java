package com.mready.domain.file.dto.common;

import lombok.Builder;

@Builder
public record FileInfo(
	String originalFilename,
	String fileUrl,
	Long fileSize,
	String contentType
) {
}

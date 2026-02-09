package com.mento.common.file.dto;

import lombok.Builder;

@Builder
public record FileInfo(
	String originalFilename,
	String fileUrl,
	Long fileSize,
	String contentType
) {
}

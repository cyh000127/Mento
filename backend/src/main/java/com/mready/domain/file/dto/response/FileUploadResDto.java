package com.mready.domain.file.dto.response;

import java.util.List;

import com.mready.domain.file.dto.common.FileInfo;

import lombok.Builder;

@Builder
public record FileUploadResDto(
	List<FileInfo> uploadedFiles
) {
}

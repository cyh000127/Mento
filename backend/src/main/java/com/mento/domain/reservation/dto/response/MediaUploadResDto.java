package com.mento.domain.reservation.dto.response;

import java.util.List;

import com.mento.common.file.dto.FileInfo;

import lombok.Builder;

@Builder
public record MediaUploadResDto(
	Long reservationId,
	List<FileInfo> uploadedFiles
) {
}
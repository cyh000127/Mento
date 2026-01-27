package com.mento.domain.reservation.converter;

import java.util.List;

import com.mento.common.file.dto.FileInfo;
import com.mento.domain.reservation.dto.response.MediaUploadResDto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReservationConverter {

	public MediaUploadResDto toMediaUploadResDto(final Long reservationId, final List<FileInfo> uploadedFiles) {
		return MediaUploadResDto.builder()
			.reservationId(reservationId)
			.uploadedFiles(uploadedFiles)
			.build();
	}
}
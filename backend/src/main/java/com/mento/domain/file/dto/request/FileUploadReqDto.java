package com.mento.domain.file.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FileUploadReqDto(

	@NotNull(message = "파일 목록은 필수입니다")
	@NotEmpty(message = "최소 1개 이상의 파일이 필요합니다")
	List<MultipartFile> files,

	@NotNull(message = "예약 ID는 필수입니다")
	Long reservationId
) {
}

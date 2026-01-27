package com.mento.domain.reservation.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.reservation.dto.response.MediaUploadResDto;
import com.mento.domain.reservation.service.ReservationFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Reservation Media", description = "예약 미디어 파일 관리 API")
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationMediaController {

	private final ReservationFacadeService reservationFacadeService;

	@Operation(summary = "예약 관련 미디어 파일 업로드", description = "예약과 관련된 이미지 및 동영상 파일을 다중 업로드합니다")
	@PostMapping(value = "/{id}/media",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<BaseResponse<MediaUploadResDto>> uploadMedia(
		@Schema(description = "예약 ID")
		@PathVariable final Long id,
		@Schema(description = "업로드할 미디어 파일 목록")
		@RequestPart("files") final List<MultipartFile> files
	) {
		MediaUploadResDto response = reservationFacadeService.uploadFiles(files, id);
		return ResponseUtils.ok(response);
	}
}

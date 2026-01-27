package com.mento.domain.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mento.common.error.ErrorCode;
import com.mento.common.file.dto.FileInfo;
import com.mento.common.file.service.FileService;
import com.mento.domain.reservation.controller.query.ReservationQueryService;
import com.mento.domain.reservation.converter.ReservationConverter;
import com.mento.domain.reservation.dto.response.MediaUploadResDto;
import com.mento.domain.reservation.exception.ReservationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationFacadeService {

	private static final String RESERVATION_DIRECTORY = "reservations/";

	private final ReservationQueryService reservationQueryService;
	private final FileService fileService;

	@Transactional
	public MediaUploadResDto uploadFiles(final List<MultipartFile> files, final Long id) {
		validateReservationExists(id);
		String directory = RESERVATION_DIRECTORY + id;
		List<FileInfo> uploadedFiles = fileService.uploadFiles(files, directory);
		log.info("[Reservation] 미디어 파일 업로드 완료 {id: {}, count: {}}", id, uploadedFiles.size());
		return ReservationConverter.toMediaUploadResDto(id, uploadedFiles);
	}

	private void validateReservationExists(final Long id) {
		if (!reservationQueryService.existById(id)) {
			throw new ReservationException(ErrorCode.RESERVATION_NOT_FOUND);
		}
	}
}

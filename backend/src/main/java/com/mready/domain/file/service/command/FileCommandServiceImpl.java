package com.mready.domain.file.service.command;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mready.common.util.CloudflareStorageUtil;
import com.mready.domain.file.converter.FileConverter;
import com.mready.domain.file.dto.common.FileInfo;
import com.mready.domain.file.dto.request.FileUploadReqDto;
import com.mready.domain.file.dto.response.FileUploadResDto;
import com.mready.domain.file.validator.FileValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileCommandServiceImpl implements FileCommandService {

	private static final String DIRECTORY_NAME = "reservations/";

	private final CloudflareStorageUtil cloudflareStorageUtil;
	private final FileValidator fileValidator;

	public FileUploadResDto uploadFiles(final FileUploadReqDto request) {
		request.files().forEach(fileValidator::validateFile);

		String directory = DIRECTORY_NAME + request.reservationId();
		List<FileInfo> uploadedFiles = request.files().stream()
			.map(file -> uploadSingleFile(file, directory))
			.toList();

		log.info("[File] 파일 업로드 완료 {reservationId: {}, count: {}}", request.reservationId(), uploadedFiles.size());

		return FileConverter.toFileUploadResDto(uploadedFiles);
	}

	private FileInfo uploadSingleFile(final MultipartFile file, final String directory) {
		String fileUrl = cloudflareStorageUtil.uploadFile(file, directory);
		return FileConverter.toFileInfo(file, fileUrl);
	}
}

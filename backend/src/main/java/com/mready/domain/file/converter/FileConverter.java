package com.mready.domain.file.converter;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mready.domain.file.dto.common.FileInfo;
import com.mready.domain.file.dto.request.FileUploadReqDto;
import com.mready.domain.file.dto.response.FileUploadResDto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileConverter {

	public FileUploadReqDto toFileUploadReqDto(final List<MultipartFile> files, final Long reservationId) {
		return FileUploadReqDto.builder()
			.files(files)
			.reservationId(reservationId)
			.build();
	}

	public FileUploadResDto toFileUploadResDto(final List<FileInfo> uploadedFiles) {
		return FileUploadResDto.builder()
			.uploadedFiles(uploadedFiles)
			.build();
	}

	public FileInfo toFileInfo(final MultipartFile file, final String fileUrl) {
		return FileInfo.builder()
			.originalFilename(file.getOriginalFilename())
			.fileUrl(fileUrl)
			.fileSize(file.getSize())
			.contentType(file.getContentType())
			.build();
	}
}

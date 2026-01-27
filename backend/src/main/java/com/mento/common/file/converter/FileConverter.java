package com.mento.common.file.converter;

import org.springframework.web.multipart.MultipartFile;

import com.mento.common.file.dto.FileInfo;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileConverter {

	public FileInfo toFileInfo(final MultipartFile file, final String fileUrl) {
		return FileInfo.builder()
			.originalFilename(file.getOriginalFilename())
			.fileUrl(fileUrl)
			.fileSize(file.getSize())
			.contentType(file.getContentType())
			.build();
	}
}

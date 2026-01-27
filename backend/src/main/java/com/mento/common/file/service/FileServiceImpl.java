package com.mento.common.file.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mento.common.file.converter.FileConverter;
import com.mento.common.file.dto.FileInfo;
import com.mento.common.file.util.CloudflareStorageUtil;
import com.mento.common.file.validator.MediaFileValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

	private final CloudflareStorageUtil cloudflareStorageUtil;
	private final MediaFileValidator mediaFileValidator;

	@Override
	public List<FileInfo> uploadFiles(final List<MultipartFile> files, final String directory) {
		files.forEach(mediaFileValidator::validateFile);

		List<FileInfo> uploadedFiles = files.stream()
			.map(file -> uploadSingleFile(file, directory))
			.toList();

		log.info("[MediaFile] 파일 업로드 완료 {directory: {}, count: {}}", directory, uploadedFiles.size());

		return uploadedFiles;
	}

	private FileInfo uploadSingleFile(final MultipartFile file, final String directory) {
		String fileUrl = cloudflareStorageUtil.uploadFile(file, directory);
		return FileConverter.toFileInfo(file, fileUrl);
	}
}

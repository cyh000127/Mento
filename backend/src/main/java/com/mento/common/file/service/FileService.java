package com.mento.common.file.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mento.common.file.dto.FileInfo;

public interface FileService {
	List<FileInfo> uploadFiles(final List<MultipartFile> files, final String directory);
}

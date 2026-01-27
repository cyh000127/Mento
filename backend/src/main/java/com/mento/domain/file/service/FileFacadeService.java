package com.mento.domain.file.service;

import org.springframework.stereotype.Service;

import com.mento.domain.file.dto.request.FileUploadReqDto;
import com.mento.domain.file.dto.response.FileUploadResDto;
import com.mento.domain.file.service.command.FileCommandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileFacadeService {

	private final FileCommandService commandService;

	public FileUploadResDto uploadFiles(final FileUploadReqDto request) {
		return commandService.uploadFiles(request);
	}
}

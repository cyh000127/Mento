package com.mready.domain.file.service.command;

import com.mready.domain.file.dto.request.FileUploadReqDto;
import com.mready.domain.file.dto.response.FileUploadResDto;

public interface FileCommandService {
	FileUploadResDto uploadFiles(final FileUploadReqDto request);
}

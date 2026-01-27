package com.mento.domain.file.service.command;

import com.mento.domain.file.dto.request.FileUploadReqDto;
import com.mento.domain.file.dto.response.FileUploadResDto;

public interface FileCommandService {
	FileUploadResDto uploadFiles(final FileUploadReqDto request);
}

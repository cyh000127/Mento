package com.mento.common.file.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mento.common.file.dto.FileInfo;
import com.mento.common.file.service.FileService;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

	private final FileService fileService;

	@Operation(summary = "단일 파일 업로드", description = "파일 하나를 업로드하고 정보를 반환합니다.")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BaseResponse<FileInfo>> uploadFile(
		@RequestPart("file") MultipartFile file
	) {
		FileInfo fileInfo = fileService.uploadFile(file, "common");
		return ResponseUtils.ok(fileInfo);
	}
}

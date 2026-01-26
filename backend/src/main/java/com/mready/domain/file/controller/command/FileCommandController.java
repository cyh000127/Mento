package com.mready.domain.file.controller.command;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mready.common.response.BaseResponse;
import com.mready.common.util.ResponseUtils;
import com.mready.domain.file.converter.FileConverter;
import com.mready.domain.file.dto.request.FileUploadReqDto;
import com.mready.domain.file.dto.response.FileUploadResDto;
import com.mready.domain.file.service.FileFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "File", description = "파일 업로드 API")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileCommandController {

	private final FileFacadeService fileFacadeService;

	@Operation(summary = "파일 업로드", description = "이미지 및 동영상 파일을 다중 업로드합니다")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BaseResponse<FileUploadResDto>> uploadFiles(
		@Schema(description = "업로드할 미디어 파일 목록")
		@RequestPart("multipartFile") final List<MultipartFile> files,
		@RequestParam final Long reservationId
	) {
		FileUploadReqDto request = FileConverter.toFileUploadReqDto(files, reservationId);
		FileUploadResDto response = fileFacadeService.uploadFiles(request);
		return ResponseUtils.created(response);
	}
}

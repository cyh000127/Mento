package com.mento.domain.livekit.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.livekit.dto.RecordingReqDto;
import com.mento.domain.livekit.dto.RecordingResDto;
import com.mento.domain.livekit.dto.RecordingStopReqDto;
import com.mento.domain.livekit.service.RecordingFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "LiveKit Recording", description = "LiveKit 녹화 관리 API")
@RestController
@RequestMapping("/api/v1/recordings")
@RequiredArgsConstructor
public class RecordingCommandController {

	private final RecordingFacadeService recordingFacadeService;

	@Operation(summary = "녹화 시작", description = "LiveKit 녹화를 시작합니다")
	@PostMapping("/start")
	public ResponseEntity<BaseResponse<RecordingResDto>> startRecording(
		@Valid @RequestBody final RecordingReqDto request
	) {
		return ResponseUtils.ok(recordingFacadeService.startRecording(request));
	}

	@Operation(summary = "녹화 중지", description = "LiveKit 녹화를 중지합니다")
	@PostMapping("/stop")
	public ResponseEntity<BaseResponse<RecordingResDto>> stopRecording(
		@Valid @RequestBody final RecordingStopReqDto request
	) {
		return ResponseUtils.ok(recordingFacadeService.stopRecording(request.egressId()));
	}

	@Operation(summary = "LiveKit 웹훅 처리", description = "LiveKit 서버에서 발생하는 이벤트를 수신합니다")
	@PostMapping("/webhook")
	public ResponseEntity<Void> handleWebhook(
		@RequestHeader("Authorization") final String authHeader,
		@RequestBody final String body
	) {
		recordingFacadeService.handleWebhook(body, authHeader);
		return ResponseEntity.ok().build();
	}
}

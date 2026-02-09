package com.mento.domain.livekit.service;

import org.springframework.stereotype.Service;

import com.mento.domain.livekit.dto.RecordingReqDto;
import com.mento.domain.livekit.dto.RecordingResDto;
import com.mento.domain.livekit.service.command.RecordingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordingFacadeService {

	private final RecordingService recordingService;

	public RecordingResDto startRecording(final RecordingReqDto request) {
		return recordingService.startRecording(request);
	}

	public RecordingResDto stopRecording(final String roomId) {
		return recordingService.stopRecording(roomId);
	}

	public void handleWebhook(final String body, final String authHeader) {
		recordingService.handleWebhook(body, authHeader);
	}
}

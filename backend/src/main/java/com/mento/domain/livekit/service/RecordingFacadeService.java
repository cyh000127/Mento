package com.mento.domain.livekit.service;

import org.springframework.stereotype.Service;

import com.mento.domain.livekit.dto.RecordingReqDto;
import com.mento.domain.livekit.dto.RecordingResDto;
import com.mento.domain.livekit.service.command.RecordingCommandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordingFacadeService {

	private final RecordingCommandService recordingCommandService;

	public RecordingResDto startRecording(final RecordingReqDto request) {
		return recordingCommandService.startRecording(request);
	}

	public RecordingResDto stopRecording(final String egressId) {
		return recordingCommandService.stopRecording(egressId);
	}

	public void handleWebhook(final String body, final String authHeader) {
		recordingCommandService.handleWebhook(body, authHeader);
	}
}

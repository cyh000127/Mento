package com.mento.domain.livekit.converter;

import com.mento.domain.livekit.dto.RecordingResDto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RecordingConverter {

	public RecordingResDto toStartResDto(final String egressId, final String roomId) {
		return RecordingResDto.builder()
			.egressId(egressId)
			.roomId(roomId)
			.status("STARTED")
			.message("녹화가 시작되었습니다")
			.build();
	}

	public RecordingResDto toStopResDto(final String egressId) {
		return RecordingResDto.builder()
			.egressId(egressId)
			.status("STOPPED")
			.message("녹화가 중지되었습니다")
			.build();
	}
}

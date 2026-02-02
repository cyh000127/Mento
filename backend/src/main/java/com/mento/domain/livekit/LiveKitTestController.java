package com.mento.domain.livekit;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.livekit.LiveKitManager;
import com.mento.common.livekit.dto.LiveKitSessionResponse;
import com.mento.domain.user.entity.Role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("checkstyle:RegexpSinglelineJava")
@Tag(name = "Test", description = "테스트용 API")
@RestController
@RequestMapping("/api/v1/test/livekit")
@RequiredArgsConstructor
public class LiveKitTestController {

	private final LiveKitManager liveKitManager;

	@Operation(summary = "LiveKit 테스트 토큰 발급", description = "인증 절차 없이 테스트용 토큰을 발급합니다.")
	@GetMapping("/token")
	public ResponseEntity<?> createTestToken(
		@RequestParam(required = false, defaultValue = "test-room") String roomName,
		@RequestParam(required = false, defaultValue = "test-user") String userName,
		@RequestParam(required = false, defaultValue = "MENTOR") Role role
	) {
		if (liveKitManager.isRoomFull(roomName, 2)) {
			return ResponseEntity.status(403).body("해당 방은 이미 2명이 참여 중입니다.");
		}

		String uniqueId = String.format("%s(%s)-%s",
			userName,
			role.name(),
			UUID.randomUUID().toString());

		long ttlSeconds = 3600;

		String token = liveKitManager.createToken(userId, userName, roomName, role, ttlSeconds);

		LiveKitSessionResponse response = new LiveKitSessionResponse(
			0L,
			token, roomName,
			liveKitManager.getUrl(),
			role.getDescription(),
			LocalDateTime.now());

		return ResponseEntity.ok(response);
	}
}

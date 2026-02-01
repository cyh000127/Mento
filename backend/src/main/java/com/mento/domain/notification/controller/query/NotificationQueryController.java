package com.mento.domain.notification.controller.query;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.service.NotificationFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notification", description = "알림 조회 API (Query)")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationQueryController {

	private final NotificationFacadeService notificationFacadeService;

	@Operation(summary = "SSE 구독", description = "서버로부터 실시간 알림을 받기 위해 SSE 연결을 구독합니다.")
	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> subscribe(
		@AuthenticationPrincipal AuthenticatedUser authuser
	) {
		SseEmitter emitter = notificationFacadeService.subscribe(authuser.getId());
		return ResponseEntity.ok(emitter);
	}

	@Operation(summary = "알림 목록 조회", description = "나의 알림 목록을 최신순으로 조회합니다.")
	@GetMapping
	public ResponseEntity<BaseResponse<List<NotificationResDto>>> getNotifications(
		@AuthenticationPrincipal AuthenticatedUser authuser
	) {
		List<NotificationResDto> response = notificationFacadeService.getNotifications(authuser.getId());
		return ResponseUtils.ok(response);
	}
}

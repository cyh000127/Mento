package com.mento.domain.notification.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.service.NotificationFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notification", description = "알림 관리 API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationCommandController {

	private final NotificationFacadeService notificationFacadeService;

	@Operation(summary = "알림 발송 (테스트용)", description = "알림을 생성하고 발송합니다.")
	// @PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/test")
	public ResponseEntity<BaseResponse<Void>> sendNotification(
		@RequestBody @Valid NotificationSendReqDto reqDto
	) {
		notificationFacadeService.sendNotification(reqDto);
		return ResponseUtils.noContent();
	}

	@Operation(summary = "알림 삭제", description = "알림을 삭제합니다.")
	@DeleteMapping("/{id}")
	public ResponseEntity<BaseResponse<Void>> deleteNotification(
		@AuthenticationPrincipal AuthenticatedUser authuser,
		@PathVariable Long id
	) {
		notificationFacadeService.deleteNotification(authuser.getId(), id);
		return ResponseUtils.noContent();
	}
}

package com.mento.common.test;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.service.NotificationFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Test - Notification", description = "알림 테스트용 API (인증 없이 알림 발송)")
@RestController
@RequestMapping("/test/v1/notifications")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationTestController {

	private final NotificationFacadeService notificationFacadeService;

	@Operation(summary = "[테스트] 알림 발송", description = "알림을 생성하고 발송합니다.")
	@PostMapping
	public ResponseEntity<BaseResponse<Void>> sendNotification(
		@RequestBody @Valid NotificationSendReqDto reqDto
	) {
		notificationFacadeService.sendNotification(reqDto);
		return ResponseUtils.noContent();
	}
}

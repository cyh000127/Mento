package com.mento.domain.notification.dto.request;

import com.mento.domain.notification.entity.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record NotificationSendReqDto(
	@NotNull(message = "사용자 ID는 필수입니다")
	Long targetMemberId,

	@NotNull(message = "알림 유형은 필수입니다")
	NotificationType type,

	@NotBlank(message = "URL은 필수입니다")
	String url,

	@NotBlank(message = "제목은 필수입니다")
	String title,

	String message
) {
}

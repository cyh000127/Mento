package com.mento.domain.payment.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PaymentApproveResDto(
	@Schema(description = "결제 ID", example = "1234567890123456")
	String paymentId,

	@Schema(description = "결제 완료 일시", example = "2026-01-31T15:30:00")
	LocalDateTime paidAt
) {
}

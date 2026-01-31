package com.mento.domain.payment.dto.response;

import java.time.LocalDateTime;

import com.mento.domain.payment.entity.PaymentMethod;
import com.mento.domain.payment.entity.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PaymentResDto(
	@Schema(description = "결제 ID", example = "1234567890123456")
	String paymentId,

	@Schema(description = "예약 ID", example = "1")
	Long reservationId,

	@Schema(description = "결제 금액 (원 단위)", example = "50000")
	Long amount,

	@Schema(description = "결제 수단", example = "KAKAO_PAY")
	PaymentMethod paymentMethod,

	@Schema(description = "결제 상태", example = "PAID")
	PaymentStatus status,

	@Schema(description = "결제 완료 일시", example = "2026-01-31T15:30:00")
	LocalDateTime paidAt,

	@Schema(description = "환불 완료 일시", example = "2026-02-01T10:00:00")
	LocalDateTime refundedAt
) {
}

package com.mento.domain.payment.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record PaymentResponseDto(
	String paymentId,
	String reservationId,
	String amount,
	String status,
	LocalDateTime paidAt,
	LocalDateTime refundedAt
) {
}

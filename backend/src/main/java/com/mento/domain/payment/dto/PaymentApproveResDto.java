package com.mento.domain.payment.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record PaymentApproveResDto(
	String paymentId,
	LocalDateTime paidAt
) {
}

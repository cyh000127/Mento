package com.mento.domain.payment.dto;

import lombok.Builder;

@Builder
public record PaymentReadyResDto(
	String paymentId,
	String redirectUrl
) {
}

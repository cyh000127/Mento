package com.mento.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PaymentApproveReqDto(
	@NotNull Long paymentId,
	@NotNull String pgToken
) {
}

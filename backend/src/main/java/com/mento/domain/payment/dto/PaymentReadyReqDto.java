package com.mento.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PaymentReadyReqDto(
	@NotNull Long reservationId,
	@NotNull String itemName,
	@NotNull Long totalAmount
) {
}

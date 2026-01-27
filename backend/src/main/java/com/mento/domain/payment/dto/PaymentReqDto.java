package com.mento.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PaymentReqDto(
	@NotNull String reservationId,
	@NotNull String itemName,
	@NotNull Integer totalAmount
) {
}

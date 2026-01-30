package com.mento.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record PaymentReadyReqDto(
	@Schema(description = "예약 ID", example = "1")
	@NotNull(message = "예약 ID는 필수입니다")
	Long reservationId,

	@Schema(description = "결제 상품명", example = "스킨케어 상담")
	@NotBlank(message = "상품명은 필수입니다")
	String itemName,

	@Schema(description = "결제 금액 (원 단위)", example = "50000")
	@NotNull(message = "결제 금액은 필수입니다")
	@Positive(message = "결제 금액은 0보다 커야 합니다")
	Long totalAmount
) {
}

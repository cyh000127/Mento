package com.mento.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PaymentApproveReqDto(
	@Schema(description = "결제 ID", example = "1234567890123456")
	@NotNull(message = "결제 ID는 필수입니다")
	Long paymentId,

	@Schema(description = "카카오페이 PG 토큰 (결제 승인 후 리다이렉트 시 전달됨)", example = "pg_token_abc123")
	@NotBlank(message = "PG 토큰은 필수입니다")
	String pgToken
) {
}

package com.mento.domain.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PaymentReadyResDto(
	@Schema(description = "생성된 결제 ID", example = "1234567890123456")
	String paymentId,

	@Schema(description = "카카오페이 결제 페이지 리다이렉트 URL")
	String redirectUrl
) {
}

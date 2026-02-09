package com.mento.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "멘토가 고객에게 아이템 추가 요청 DTO")
public record MentorAddItemReqDto(
	@NotNull(message = "상품 ID는 필수입니다")
	@Schema(description = "상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long productId,

	@NotNull(message = "예약 ID는 필수입니다")
	@Schema(description = "예약 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long reservationId
) {
}

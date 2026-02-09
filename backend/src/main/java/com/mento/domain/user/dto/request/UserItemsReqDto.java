package com.mento.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "사용자 아이템 목록 조회 요청 DTO")
public record UserItemsReqDto(
	@NotNull
	@Schema(description = "예약 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long reservationId,


	@Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
	Integer page,

	@Schema(description = "페이지 크기", example = "10", defaultValue = "10")
	Integer size
) {
}

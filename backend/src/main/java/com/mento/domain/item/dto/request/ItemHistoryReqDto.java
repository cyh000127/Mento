package com.mento.domain.item.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
@Schema(description = "아이템 히스토리 조회 요청 DTO")
public record ItemHistoryReqDto(
	@Schema(description = "특정 상품 ID 필터", example = "500")
	Long productId,

	@Schema(description = "조회 시작 날짜", example = "2026-01-01")
	LocalDate startDate,

	@Schema(description = "조회 종료 날짜", example = "2026-01-31")
	LocalDate endDate,

	@PositiveOrZero
	@Schema(description = "페이지 번호 (0부터 시작)", defaultValue = "0")
	Integer page,

	@PositiveOrZero
	@Schema(description = "페이지 크기", defaultValue = "20")
	Integer size
) {
}
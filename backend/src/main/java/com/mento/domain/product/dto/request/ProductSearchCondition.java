package com.mento.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductSearchCondition(
	@Schema(description = "브랜드명 필터", example = "이니스프리")
	String brand,

	@Schema(description = "중분류 카테고리", example = "스킨케어")
	String categoryMedium,

	@Schema(description = "소분류 카테고리", example = "토너/스킨")
	String categorySmall,

	@Schema(description = "정렬 기준 (price, name, created_at)", example = "price")
	String sortKey,

	@Schema(description = "정렬 순서 (asc, desc)", example = "asc")
	String order
) {
}

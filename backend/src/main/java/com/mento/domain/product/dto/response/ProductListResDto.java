package com.mento.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ProductListResDto(
	@Schema(description = "상품 ID", example = "1")
	Long productId,

	@Schema(description = "상품명", example = "그린티 씨드 스킨")
	String name,

	@Schema(description = "브랜드명", example = "이니스프리")
	String brandName,

	@Schema(description = "중분류", example = "스킨케어")
	String categoryMedium,

	@Schema(description = "이미지 URL", example = "https://s3.amazonaws.com/bucket/products/1.jpg")
	String imageUrl
) {
}

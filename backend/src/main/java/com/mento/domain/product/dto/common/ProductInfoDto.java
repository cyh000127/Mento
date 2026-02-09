package com.mento.domain.product.dto.common;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "상품 정보")
public record ProductInfoDto(
	@Schema(description = "상품 ID", example = "1")
	Long id,

	@Schema(description = "상품명", example = "닥터지 레드 블레미쉬 클리어 수딩 크림", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	String name,

	@Schema(description = "중분류", example = "스킨케어", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	String categoryMedium,

	@Schema(description = "상품 이미지 URL", example = "https://example.com/image.jpg")
	@URL
	String imageUrl,

	@Schema(description = "상품 페이지 URL", example = "https://example.com/product/1")
	@URL
	String productUrl
) {
}

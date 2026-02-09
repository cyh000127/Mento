package com.mento.domain.item.dto.response;

import com.mento.domain.item.enums.ItemStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "아이템 페이지 응답 DTO")
public record ItemPageResDto(
	@NotNull
	@Schema(description = "아이템 ID", example = "1")
	Long itemId,

	@Schema(description = "상품 Id", example = "2")
	Long productId,

	@NotNull
	@Schema(description = "상품명", example = "토너 패드")
	String productName,

	@Schema(description = "상품 이미지 URL", example = "https://example.com/image.jpg", nullable = true)
	String productImageUrl,

	@Schema(description = "브랜드명", example = "토리든", nullable = true)
	String brandName,

	@NotNull
	@Schema(description = "아이템 상태", example = "OWNED")
	ItemStatus status,

	@NotNull
	@Schema(description = "즐겨찾기 여부", example = "true")
	Boolean isFavorite
) {
}

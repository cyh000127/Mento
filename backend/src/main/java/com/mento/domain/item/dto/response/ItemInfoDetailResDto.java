package com.mento.domain.item.dto.response;

import java.time.LocalDate;

import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.product.dto.common.ProductInfoDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "아이템 상세 정보 응답 DTO")
public record ItemInfoDetailResDto(
	@NotNull
	@Schema(description = "아이템 ID", example = "1")
	Long id,

	@NotNull
	@Schema(description = "사용자 ID", example = "1")
	Long userId,

	@NotNull
	@Schema(description = "상품 상세 정보")
	ProductInfoDto productInfoDto,

	@NotNull
	@Schema(description = "아이템 상태", example = "OWNED")
	ItemStatus status,

	@NotNull
	@Schema(description = "즐겨찾기 여부", example = "true")
	Boolean isFavorite,

	@NotNull
	@Schema(description = "구매일", example = "2026-01-01")
	LocalDate purchaseDate,

	@NotNull
	@Schema(description = "예상 만료일", example = "2026-04-01")
	LocalDate expectedExpiry,

	@NotNull
	@Schema(description = "구매 횟수", example = "3")
	Integer purchaseCount
) {
}
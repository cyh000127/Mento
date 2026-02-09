package com.mento.domain.item.dto.response;

import java.time.LocalDateTime;

import com.mento.domain.item.enums.ItemHistoryAction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "아이템 히스토리 응답 DTO")
public record ItemHistoryResDto(
	@NotNull
	@Schema(description = "히스토리 ID", example = "5001")
	Long historyId,

	@NotNull
	@Schema(description = "상품 ID", example = "500")
	Long productId,

	@NotNull
	@Schema(description = "상품명", example = "세라마이드 수분크림")
	String productName,

	@Schema(description = "브랜드명", example = "브랜드A", nullable = true)
	String brandName,

	@Schema(description = "상품 이미지 URL", nullable = true)
	String imageUrl,

	@NotNull
	@Schema(description = "액션 타입", example = "CREATED")
	ItemHistoryAction actionType,

	@NotNull
	@Schema(description = "액션 설명", example = "인벤토리에 추가됨")
	String actionDescription,

	@NotNull
	@Schema(description = "생성 일시", example = "2026-01-20T10:00:00")
	LocalDateTime createdAt
) {
}

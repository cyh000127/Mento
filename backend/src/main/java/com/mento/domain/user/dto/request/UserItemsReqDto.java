package com.mento.domain.user.dto.request;

import com.mento.domain.item.enums.ItemCategory;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.enums.SortType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
@Schema(description = "사용자 아이템 목록 조회 요청 DTO")
public record UserItemsReqDto(
	@NotNull
	@Schema(description = "예약 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long reservationId,

	@Schema(description = "아이템 상태 필터", example = "OWNED")
	ItemStatus status,

	@Schema(description = "아이템 카테고리 필터", example = "SKIN")
	ItemCategory category,

	@Schema(description = "즐겨찾기 필터", example = "true")
	Boolean isFavorite,

	@Schema(description = "정렬 방식", example = "LATEST", defaultValue = "LATEST")
	SortType sortType,

	@NotNull
	@PositiveOrZero
	@Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
	Integer page,

	@NotNull
	@Positive
	@Schema(description = "페이지 크기", example = "10", defaultValue = "10")
	Integer size
) {
}

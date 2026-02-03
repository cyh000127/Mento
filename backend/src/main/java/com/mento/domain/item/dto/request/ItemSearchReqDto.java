package com.mento.domain.item.dto.request;

import com.mento.domain.item.enums.ItemCategory;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.enums.SortType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;


@Builder
@Schema(description = "아이템 검색 조건")
public record ItemSearchReqDto(
	@Schema(description = "아이템 상태 필터링 (선택)", example = "OWNED")
	ItemStatus status,

	@Schema(description = "아이템 카테고리 (선택)", example = "HAIR")
	ItemCategory category,

	@Schema(description = "즐겨찾기 필터링 (선택)", example = "true")
	Boolean isFavorite,

	@Schema(description = "정렬 방식 (important: 유통기한 임박순, latest: 최신 등록순, purchaseDate: 구매일순)",
		example = "latest", defaultValue = "latest")
	String sort,

	@Min(0)
	@Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
	Integer page,

	@Min(1)
	@Max(100)
	@Schema(description = "페이지 크기 (1~100)", example = "20", defaultValue = "10")
	Integer size
) {
	/**
	 * 기본값 적용 Compact Constructor
	 */
	public ItemSearchReqDto {
		if (page == null || page < 0) {
			page = 0;
		}
		if (size == null || size < 1) {
			size = 10;
		}
		if (size > 100) {
			size = 100;
		}
	}

	/**
	 * String sort를 SortType enum으로 변환
	 */
	public SortType getSortType() {
		return SortType.from(sort);
	}
}

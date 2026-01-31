package com.mento.domain.inventory.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ItemStatus {
	OWNED("소유중"),
	UNAVAILABLE("사용불가"),
	PURCHASING("구매중"),
	RECOMMENDED("추천받음"),
	OVER_DATED("사용기간 만료");

	private final String description;
}

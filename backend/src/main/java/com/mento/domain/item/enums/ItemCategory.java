package com.mento.domain.item.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ItemCategory {
	SKIN("스킨케어"),
	HAIR("헤어케어"),
	BEAUTY("메이크업");

	private final String description;
}

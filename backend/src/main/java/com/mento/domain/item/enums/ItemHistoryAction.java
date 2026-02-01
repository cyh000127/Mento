package com.mento.domain.item.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemHistoryAction {
	CREATED("인벤토리에 추가됨"),
	EXPIRED("사용기간 만료됨"),
	DELETED("인벤토리에서 삭제됨");

	private final String description;
}
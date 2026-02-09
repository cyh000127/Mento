package com.mento.domain.item.enums;

import java.util.Arrays;

import org.springframework.data.domain.Sort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SortType {

	IMPORTANT("important", "유통기한 임박순", Sort.by(Sort.Direction.ASC, "expectedExpiryDate", "id")),
	LATEST("latest", "최신 등록순", Sort.by(Sort.Direction.DESC, "createdAt", "id")),
	PURCHASE_DATE("purchaseDate", "구매일순", Sort.by(Sort.Direction.DESC, "purchaseDate", "id"));

	private final String value;
	private final String description;
	private final Sort sort;

	public static SortType from(final String value) {
		if (value == null || value.isBlank()) {
			return LATEST;
		}

		return Arrays.stream(values())
			.filter(type -> type.value.equalsIgnoreCase(value))
			.findFirst()
			.orElse(LATEST);
	}
}
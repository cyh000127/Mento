package com.mento.domain.item.enums;

import java.util.Arrays;

import org.springframework.data.domain.Sort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SortType {
	IMPORTANT("expectedExpiryDate", Sort.by(Sort.Direction.ASC, "expectedExpiryDate", "id")),
	LATEST("latest", Sort.by(Sort.Direction.DESC, "createdAt", "id")),
	PURCHASE_DATE("purchaseDate", Sort.by(Sort.Direction.DESC, "purchaseDate", "id"));

	private final String value;
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
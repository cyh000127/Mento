package com.mento.domain.user.entity;

import com.mento.common.error.ErrorCode;
import com.mento.domain.user.exception.UserException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Role {
	USER("USER"),
	MENTOR("MENTOR"),
	ADMIN("ADMIN");

	private final String description;

	public static Role fromString(final String role) {
		if (role == null || role.isBlank()) {
			throw new UserException(ErrorCode.INVALID_USER_ROLE);
		}

		String normalizedRole = role.toUpperCase();
		return switch (normalizedRole) {
			case "USER" -> USER;
			case "MENTO", "MENTOR" -> MENTOR;
			case "ADMIN" -> ADMIN;
			default -> throw new UserException(ErrorCode.INVALID_USER_ROLE);
		};
	}
}

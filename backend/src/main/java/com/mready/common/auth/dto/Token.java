package com.mready.common.auth.dto;

import lombok.Builder;

@Builder
public record Token(
	String accessToken,
	String refreshToken
) {
}

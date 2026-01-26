package com.mready.common.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
	@NotBlank
	String secret,
	@NotNull
	Long accessTokenExpiration,
	@NotNull
	Long refreshTokenExpiration
) {
}

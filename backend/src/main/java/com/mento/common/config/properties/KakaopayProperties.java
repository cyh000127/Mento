package com.mento.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "kakaopay")
public record KakaopayProperties(
	@NotBlank(message = "베이스 URL은 필수입니다.")
	String baseUrl,
	@NotBlank(message = "Secret Key는 필수입니다.")
	String secretKey,
	@NotBlank(message = "가맹점 코드는 필수입니다.")
	String cid,

	RedirectUrls redirectUrls
) {
	public record RedirectUrls(String approval, String cancel, String fail) {
	}
}

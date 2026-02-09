package com.mento.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "skinai")
public record SkinAnalysisProperties(
	@NotBlank(message = "베이스 URL은 필수입니다.")
	String baseUrl
) {
}

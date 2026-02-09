package com.mento.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "ai.prompts")
public record PromptProperties(
	@NotNull(message = "시스템 프롬프트는 NULL 일 수 없습니다.")
	Resource system,

	@NotNull(message = "컨설팅 프롬프트는 NULL 일 수 없습니다.")
	Resource consulting
) {
}
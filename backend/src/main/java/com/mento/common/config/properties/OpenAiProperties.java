package com.mento.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties("spring.ai.openai")
public record OpenAiProperties(
	@NotBlank
	String apiKey,
	@NotBlank
	String baseUrl,
	@Valid
	@NotNull
	Chat chat
) {
	public record Chat(
		@NotBlank
		String completionsPath,
		@Valid
		@NotNull
		Options options
	) {
	}

	public record Options(
		@NotBlank
		String model
	) {
	}
}
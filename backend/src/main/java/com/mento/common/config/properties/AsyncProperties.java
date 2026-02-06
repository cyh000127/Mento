package com.mento.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

@Validated
@ConfigurationProperties(prefix = "async.ai-upload")
public record AsyncProperties(
	@Min(1) int corePoolSize,
	@Min(1) int maxPoolSize,
	@Min(1) int queueCapacity
) {
}
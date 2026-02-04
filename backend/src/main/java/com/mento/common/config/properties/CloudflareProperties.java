package com.mento.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "cloudflare.r2")
public record CloudflareProperties(
	@NotBlank(message = "Cloudflare R2 엔드포인트는 필수입니다")
	String endpoint,

	@NotBlank(message = "R2 버킷 이름은 필수입니다")
	String bucket,

	@NotBlank(message = "R2 액세스 키는 필수입니다")
	String accessKey,

	@NotBlank(message = "R2 시크릿 키는 필수입니다")
	String secretKey,

	@NotBlank(message = "outerPrefix는 필수입니다.")
	String outerPrefix
) {
}

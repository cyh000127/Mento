package com.mento.domain.product.dto.common;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ProductInfoDto(
	Long id,

	@NotBlank
	String name,

	@URL
	String imageUrl,

	@URL
	String productUrl
) {
}

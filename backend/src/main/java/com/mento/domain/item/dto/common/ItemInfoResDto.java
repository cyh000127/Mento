package com.mento.domain.item.dto.common;

import java.time.LocalDate;

import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.product.dto.common.ProductInfoDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record ItemInfoResDto(
	Long id,

	ProductInfoDto productInfo,

	@NotNull
	ItemStatus status,

	@NotNull
	Boolean isFavorite,

	@NotNull @Positive
	Integer purchaseCount,

	@NotNull
	LocalDate purchaseDate,

	@NotNull @PastOrPresent
	LocalDate expectedExpiryDate
) {
}

package com.mento.domain.item.converter;

import com.mento.domain.item.dto.common.ItemInfoResDto;
import com.mento.domain.item.entity.Item;
import com.mento.domain.product.converter.ProductConverter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemConverter {
	public static ItemInfoResDto toItemInfoResDto(final Item item) {
		return ItemInfoResDto.builder()
			.id(item.getId())
			.productInfo(ProductConverter.toProductInfoDto(item.getProduct()))
			.status(item.getStatus())
			.isFavorite(item.getIsFavorite())
			.purchaseCount(item.getPurchaseCount())
			.purchaseDate(item.getPurchaseDate())
			.expectedExpiryDate(item.getExpectedExpiryDate())
			.build();
	}
}

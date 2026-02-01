package com.mento.domain.item.converter;

import com.mento.domain.item.dto.common.ItemInfoResDto;
import com.mento.domain.item.dto.response.ItemHistoryResDto;
import com.mento.domain.item.dto.response.ItemInfoDetailResDto;
import com.mento.domain.item.dto.response.ItemPageResDto;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.entity.ItemHistory;
import com.mento.domain.product.converter.ProductConverter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemConverter {

	public ItemInfoResDto toItemInfoResDto(final Item item) {
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

	public ItemPageResDto toItemPageResDto(final Item item) {
		return ItemPageResDto.builder()
			.id(item.getId())
			.productName(item.getProduct().getName())
			.productImageUrl(item.getProduct().getImageUrl())
			.brandName(item.getProduct().getBrand().getBrandName())
			.status(item.getStatus())
			.isFavorite(item.getIsFavorite())
			.build();
	}

	public ItemInfoDetailResDto toItemInfoDetailResDto(final Item item) {
		return ItemInfoDetailResDto.builder()
			.id(item.getId())
			.userId(item.getUser().getId())
			.productInfoDto(ProductConverter.toProductInfoDto(item.getProduct()))
			.status(item.getStatus())
			.isFavorite(item.getIsFavorite())
			.purchaseDate(item.getPurchaseDate())
			.expectedExpiry(item.getExpectedExpiryDate())
			.purchaseCount(item.getPurchaseCount())
			.build();
	}

	public ItemHistoryResDto toItemHistoryResDto(final ItemHistory history) {
		return ItemHistoryResDto.builder()
			.historyId(history.getId())
			.productId(history.getProduct().getId())
			.productName(history.getProduct().getName())
			.brandName(history.getProduct().getBrand().getBrandName())
			.imageUrl(history.getProduct().getImageUrl())
			.actionType(history.getActionType())
			.actionDescription(history.getActionType().getDescription())
			.createdAt(history.getCreatedAt())
			.build();
	}
}

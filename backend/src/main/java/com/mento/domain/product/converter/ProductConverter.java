package com.mento.domain.product.converter;

import com.mento.domain.product.dto.request.ProductCreateReqDto;
import com.mento.domain.product.dto.response.ProductResDto;
import com.mento.domain.product.entity.Product;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductConverter {

	public Product toEntity(final ProductCreateReqDto dto) {
		return Product.builder()
			.brandId(dto.brandId())
			.oliveyoungGoodsNo(dto.oliveyoungGoodsNo())
			.categoryMedium(dto.categoryMedium())
			.categorySmall(dto.categorySmall())
			.name(dto.name())
			.volume(dto.volume())
			.description(dto.description())
			.ingredients(dto.ingredients())
			.price(dto.price())
			.imageUrl(dto.imageUrl())
			.productUrl(dto.productUrl())
			.skinTypes(dto.skinTypes())
			.relatedConditions(dto.relatedConditions())
			.benefits(dto.benefits())
			.defaultUsageDays(dto.defaultUsageDays())
			.build();
	}

	public ProductResDto toProductResDto(final Product entity, final String brandName) {
		return ProductResDto.builder()
			.id(entity.getId())
			.brandId(entity.getBrandId())
			.brandName(brandName)
			.oliveyoungGoodsNo(entity.getOliveyoungGoodsNo())
			.categoryMedium(entity.getCategoryMedium())
			.categorySmall(entity.getCategorySmall())
			.name(entity.getName())
			.volume(entity.getVolume())
			.description(entity.getDescription())
			.ingredients(entity.getIngredients())
			.price(entity.getPrice())
			.imageUrl(entity.getImageUrl())
			.productUrl(entity.getProductUrl())
			.skinTypes(entity.getSkinTypes())
			.relatedConditions(entity.getRelatedConditions())
			.benefits(entity.getBenefits())
			.defaultUsageDays(entity.getDefaultUsageDays())
			.createdAt(entity.getCreatedAt())
			.updatedAt(entity.getUpdatedAt())
			.build();
	}
}

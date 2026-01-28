package com.mento.domain.product.service;

import com.mento.common.error.ErrorCode;
import com.mento.domain.brand.entity.Brand;
import com.mento.domain.brand.exception.BrandException;
import com.mento.domain.brand.repository.BrandRepository;
import com.mento.domain.product.converter.ProductConverter;
import com.mento.domain.product.dto.request.ProductCreateReqDto;
import com.mento.domain.product.dto.response.ProductResDto;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.service.command.ProductCommandService;
import com.mento.domain.product.service.query.ProductQueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductFacadeService {

	private final ProductCommandService productCommandService;
	private final ProductQueryService productQueryService;
	private final BrandRepository brandRepository;

	public ProductResDto createProduct(final ProductCreateReqDto reqDto) {
		Product product = ProductConverter.toEntity(reqDto);
		Product savedProduct = productCommandService.create(product);
		String brandName = getBrandName(savedProduct.getBrandId());
		return ProductConverter.toProductResDto(savedProduct, brandName);
	}

	public ProductResDto getProduct(final Long id) {
		Product product = productQueryService.findById(id);
		String brandName = getBrandName(product.getBrandId());
		return ProductConverter.toProductResDto(product, brandName);
	}

	private String getBrandName(final Long brandId) {
		return brandRepository.findById(brandId)
			.map(Brand::getName)
			.orElseThrow(() -> new BrandException(ErrorCode.BRAND_NOT_FOUND));
	}
}

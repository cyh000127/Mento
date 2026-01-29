package com.mento.domain.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mento.domain.brand.entity.Brand;
import com.mento.domain.brand.service.query.BrandQueryService;
import com.mento.domain.product.converter.ProductConverter;
import com.mento.domain.product.dto.request.ProductCreateReqDto;
import com.mento.domain.product.dto.request.ProductSearchCondition;
import com.mento.domain.product.dto.response.ProductResDto;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.service.command.ProductCommandService;
import com.mento.domain.product.service.query.ProductQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductFacadeService {

	private final ProductCommandService productCommandService;
	private final ProductQueryService productQueryService;
	private final BrandQueryService brandQueryService;

	public ProductResDto createProduct(final ProductCreateReqDto reqDto) {
		Brand brand = brandQueryService.getBrand(reqDto.brandId());
		Product product = ProductConverter.toEntity(reqDto, brand);
		Product savedProduct = productCommandService.create(product);
		return ProductConverter.toProductResDto(savedProduct);
	}

	public ProductResDto getProduct(final Long id) {
		Product product = productQueryService.findById(id);
		return ProductConverter.toProductResDto(product);
	}

	public Page<ProductResDto> getProducts(final ProductSearchCondition condition, final int page, final int size) {
		Sort sort = createSort(condition.sortKey(), condition.order());
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Product> products = productQueryService.getProducts(condition, pageable);
		return products.map(ProductConverter::toProductResDto);
	}

	private Sort createSort(final String sortKey, final String order) {
		String property = "name";
		if ("price".equals(sortKey)) {
			property = "price";
		} else if ("createdAt".equals(sortKey)) {
			property = "createdAt";
		}

		Sort.Direction direction = Sort.Direction.ASC;
		if ("desc".equalsIgnoreCase(order)) {
			direction = Sort.Direction.DESC;
		}

		return Sort.by(direction, property);
	}
}

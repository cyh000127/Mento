package com.mento.domain.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mento.domain.brand.entity.Brand;
import com.mento.domain.brand.service.query.BrandQueryService;
import com.mento.domain.product.converter.ProductConverter;
import com.mento.domain.product.dto.request.ProductCreateReqDto;
import com.mento.domain.product.dto.response.ProductListResDto;
import com.mento.domain.product.dto.response.ProductResDto;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.service.command.ProductCommandService;
import com.mento.domain.product.service.query.ProductQueryService;
import com.mento.domain.product.service.search.ProductSearchService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductFacadeService {

	private final ProductCommandService productCommandService;
	private final ProductQueryService productQueryService;
	private final ProductSearchService productSearchService;
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

	public Page<ProductListResDto> getProducts(final int page, final int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productQueryService.getProducts(pageable);
	}

	public Page<ProductListResDto> search(String query, Pageable pageable) {
		Page<Product> productPage = productSearchService.search(query, pageable);
		return productPage.map(ProductConverter::toProductListResDto);
	}
}

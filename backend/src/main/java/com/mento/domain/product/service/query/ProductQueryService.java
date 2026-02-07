package com.mento.domain.product.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.product.dto.response.ProductListResDto;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.exception.ProductException;
import com.mento.domain.product.repository.ProductRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class ProductQueryService {

	private final ProductRepository productRepository;

	public Product findById(final Long id) {
		return productRepository.findById(id)
			.orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	public Product findDetailById(final Long id) {
		return productRepository.findWithBrandById(id)
			.orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	public Page<ProductListResDto> getProducts(final Pageable pageable) {
		return productRepository.findAllProductsProjected(pageable);
	}
}

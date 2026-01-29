package com.mento.domain.product.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.product.dto.request.ProductSearchCondition;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.exception.ProductException;
import com.mento.domain.product.repository.ProductRepository;
import com.mento.domain.product.repository.ProductSpecification;

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

	public Page<Product> getProducts(final ProductSearchCondition condition, final Pageable pageable) {
		Specification<Product> spec = ProductSpecification.search(condition);
		return productRepository.findAll(spec, pageable);
	}
}

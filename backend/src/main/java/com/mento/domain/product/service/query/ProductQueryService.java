package com.mento.domain.product.service.query;

import com.mento.common.error.ErrorCode;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.exception.ProductException;
import com.mento.domain.product.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class ProductQueryService {

	private final ProductRepository productRepository;

	public Product findById(final Long id) {
		return productRepository.findById(id)
			.orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));
	}
}

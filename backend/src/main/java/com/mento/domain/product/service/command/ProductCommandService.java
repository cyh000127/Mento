package com.mento.domain.product.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.product.entity.Product;
import com.mento.domain.product.repository.ProductRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCommandService {

	private final ProductRepository productRepository;

	public Product create(final Product product) {
		Product savedProduct = productRepository.save(product);
		log.info("[Product] 상품 생성 완료 {id: {}, name: {}}", savedProduct.getId(), savedProduct.getName());
		return savedProduct;
	}
}

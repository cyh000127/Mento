package com.mento.domain.product.service.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mento.domain.product.entity.Product;

public interface ProductSearchService {
	Page<Product> search(String refinedQuery, Pageable pageable);
}
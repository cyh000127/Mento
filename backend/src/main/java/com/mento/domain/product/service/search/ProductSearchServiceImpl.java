package com.mento.domain.product.service.search;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.product.entity.Product;
import com.mento.domain.product.entity.ProductDocument;
import com.mento.domain.product.repository.ProductRepository;
import com.mento.domain.product.util.ProductSearchQueryBuilder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class ProductSearchServiceImpl implements ProductSearchService {

	private final ElasticsearchOperations operations;
	private final ProductRepository productRepository;

	public Page<Product> search(final String refinedQuery, final Pageable pageable) {
		NativeQuery query = ProductSearchQueryBuilder.build(refinedQuery, pageable);
		SearchHits<ProductDocument> searchHits = operations.search(query, ProductDocument.class);

		if (searchHits.getTotalHits() <= 0) {
			return Page.empty(pageable);
		}

		List<Long> productIds = searchHits.stream()
			.map(hit -> hit.getContent().getProductId())
			.toList();

		List<Product> sortedProducts = fetchAndSortProducts(productIds);

		return new PageImpl<>(sortedProducts, pageable, searchHits.getTotalHits());
	}

	private List<Product> fetchAndSortProducts(final List<Long> productIds) {
		List<Product> products = productRepository.findAllByIdIn(productIds);

		Map<Long, Product> productMap = products.stream()
			.collect(Collectors.toMap(Product::getId, Function.identity()));

		return productIds.stream()
			.map(productMap::get)
			.filter(Objects::nonNull)
			.toList();
	}
}
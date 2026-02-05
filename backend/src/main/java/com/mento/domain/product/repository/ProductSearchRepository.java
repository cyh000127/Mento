package com.mento.domain.product.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.mento.domain.product.entity.ProductDocument;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
}

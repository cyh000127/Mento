package com.mento.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mento.domain.product.dto.response.ProductListResDto;
import com.mento.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("""
		SELECT new com.mento.domain.product.dto.response.ProductListResDto(
			p.id, p.name, b.brandName, p.categoryMedium, p.imageUrl
		)
		FROM Product p
		JOIN p.brand b
		""")
	Page<ProductListResDto> findAllProductsProjected(Pageable pageable);
}

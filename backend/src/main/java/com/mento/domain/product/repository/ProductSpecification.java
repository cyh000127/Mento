package com.mento.domain.product.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.mento.domain.product.dto.request.ProductSearchCondition;
import com.mento.domain.product.entity.Product;

import jakarta.persistence.criteria.JoinType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSpecification {

	public static Specification<Product> search(ProductSearchCondition condition) {
		return (root, query, builder) -> {
			if (Long.class != query.getResultType()) {
				root.fetch("brand", JoinType.INNER);
			}

			Specification<Product> spec = (_, _, _) -> null;

			if (StringUtils.hasText(condition.categoryMedium())) {
				spec = spec.and((r, _, b) -> b.equal(r.get("categoryMedium"), condition.categoryMedium()));
			}

			if (StringUtils.hasText(condition.categorySmall())) {
				spec = spec.and((r, _, b) -> b.equal(r.get("categorySmall"), condition.categorySmall()));
			}

			if (StringUtils.hasText(condition.brand())) {
				spec = spec.and((r, _, b) -> b.like(r.get("brand").get("brandName"), "%" + condition.brand() + "%"));
			}

			return spec.toPredicate(root, query, builder);
		};
	}
}

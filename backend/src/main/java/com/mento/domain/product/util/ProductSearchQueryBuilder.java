package com.mento.domain.product.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductSearchQueryBuilder {

	private static final String FIELD_NAME = "name";
	private static final String FIELD_BRAND = "brandName";
	private static final float BOOST_NAME = 5.0f;
	private static final float BOOST_BRAND = 2.0f;
	private static final String MINIMUM_SHOULD_MATCH = "1";

	public NativeQuery build(final String searchKeyword, final Pageable pageable) {
		return NativeQuery.builder()
			.withQuery(q -> q.bool(b -> b
				.minimumShouldMatch(MINIMUM_SHOULD_MATCH)
				.should(s -> s.match(m -> m
					.field(FIELD_NAME)
					.query(searchKeyword)
					.boost(BOOST_NAME)
				))
				.should(s -> s.match(m -> m
					.field(FIELD_BRAND)
					.query(searchKeyword)
					.boost(BOOST_BRAND)
				))
				.should(s -> s.match(m -> m
					.field(FIELD_NAME)
					.query(searchKeyword)
					.fuzziness("AUTO")
				))
			))
			.withPageable(pageable)
			// 네트워크 최적화를 위해 ID만 가져오기
			.withSourceFilter(new FetchSourceFilter(true, new String[] {"productId"}, null))
			.build();
	}
}
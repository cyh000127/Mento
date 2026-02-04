package com.mento.domain.product.service.query;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mento.common.error.ErrorCode;
import com.mento.domain.product.dto.response.ProductListResDto;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.exception.ProductException;
import com.mento.domain.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductQueryService 단위 테스트")
class ProductQueryServiceTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductQueryService productQueryService;

	@Test
	@DisplayName("상품_단건_조회_성공")
	void 상품_단건_조회_성공() {
		// given
		Long productId = 1L;
		Product product = Product.builder().id(productId).name("Test Product").build();

		given(productRepository.findById(productId)).willReturn(Optional.of(product));

		// when
		Product result = productQueryService.findById(productId);

		// then
		assertThat(result.getId()).isEqualTo(productId);
		assertThat(result.getName()).isEqualTo("Test Product");
		then(productRepository).should(times(1)).findById(productId);
	}

	@Test
	@DisplayName("상품_단건_조회_실패_없음")
	void 상품_단건_조회_실패_없음() {
		// given
		Long productId = 999L;
		given(productRepository.findById(productId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productQueryService.findById(productId))
			.isInstanceOf(ProductException.class)
			.hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("상품_목록_조회_성공")
	void 상품_목록_조회_성공() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductListResDto> emptyPage = Page.empty();

		// Repository: findAllProductsProjected(pageable)
		given(productRepository.findAllProductsProjected(pageable))
			.willReturn(emptyPage);

		// when
		Page<ProductListResDto> result = productQueryService.getProducts(pageable);

		// then
		assertThat(result).isEmpty();
		then(productRepository).should(times(1)).findAllProductsProjected(eq(pageable));
	}
}

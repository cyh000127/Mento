package com.mento.domain.product.service.command;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.domain.product.entity.Product;
import com.mento.domain.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCommandService 단위 테스트")
class ProductCommandServiceTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductCommandService productCommandService;

	@Test
	@DisplayName("상품_저장_성공")
	void 상품_저장_성공() {
		// given
		Product product = Product.builder().name("New Product").build();
		Product savedProduct = Product.builder().id(1L).name("New Product").build();

		given(productRepository.save(product)).willReturn(savedProduct);

		// when
		Product result = productCommandService.create(product);

		// then
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("New Product");
		then(productRepository).should(times(1)).save(product);
	}
}

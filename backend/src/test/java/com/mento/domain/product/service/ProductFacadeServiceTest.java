package com.mento.domain.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mento.common.error.ErrorCode;
import com.mento.domain.brand.entity.Brand;
import com.mento.domain.brand.exception.BrandException;
import com.mento.domain.brand.repository.BrandRepository;
import com.mento.domain.product.dto.request.ProductCreateReqDto;
import com.mento.domain.product.dto.request.ProductSearchCondition;
import com.mento.domain.product.dto.response.ProductResDto;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.service.command.ProductCommandService;
import com.mento.domain.product.service.query.ProductQueryService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductFacadeService 단위 테스트")
class ProductFacadeServiceTest {

	@Mock
	private ProductCommandService productCommandService;

	@Mock
	private ProductQueryService productQueryService;

	@Mock
	private BrandRepository brandRepository;

	@InjectMocks
	private ProductFacadeService productFacadeService;

	@Test
	@DisplayName("상품_생성_성공")
	void 상품_생성_성공() {
		// given
		Long brandId = 1L;
		Brand brand = Brand.builder().id(brandId).brandName("Test Brand").build();
		ProductCreateReqDto reqDto = ProductCreateReqDto.builder()
			.brandId(brandId)
			.name("Test Product")
			.price(10000)
			.defaultUsageDays(90)
			.oliveyoungGoodsNo("12345")
			.build();

		Product product = Product.builder()
			.id(1L)
			.brand(brand)
			.name("Test Product")
			.build();

		given(brandRepository.findById(brandId)).willReturn(Optional.of(brand));
		given(productCommandService.create(any(Product.class))).willReturn(product);

		// when
		ProductResDto result = productFacadeService.createProduct(reqDto);

		// then
		assertThat(result.name()).isEqualTo("Test Product");
		assertThat(result.brandName()).isEqualTo("Test Brand");
		then(brandRepository).should(times(1)).findById(brandId);
		then(productCommandService).should(times(1)).create(any(Product.class));
	}

	@Test
	@DisplayName("상품_생성_실패_브랜드_없음")
	void 상품_생성_실패_브랜드_없음() {
		// given
		Long brandId = 999L;
		ProductCreateReqDto reqDto = ProductCreateReqDto.builder()
			.brandId(brandId)
			.name("Test Product")
			.oliveyoungGoodsNo("12345")
			.price(10000)
			.defaultUsageDays(90)
			.build();

		given(brandRepository.findById(brandId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productFacadeService.createProduct(reqDto))
			.isInstanceOf(BrandException.class)
			.hasMessageContaining(ErrorCode.BRAND_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("상품_단건_조회_성공")
	void 상품_단건_조회_성공() {
		// given
		Long productId = 1L;
		Brand brand = Brand.builder().id(1L).brandName("Brand").build();
		Product product = Product.builder().id(productId).brand(brand).name("Product").build();

		given(productQueryService.findById(productId)).willReturn(product);

		// when
		ProductResDto result = productFacadeService.getProduct(productId);

		// then
		assertThat(result.id()).isEqualTo(productId);
		assertThat(result.name()).isEqualTo("Product");
		then(productQueryService).should(times(1)).findById(productId);
	}

	@Test
	@DisplayName("상품_목록_조회_정렬_테스트_가격_내림차순")
	void 상품_목록_조회_정렬_테스트_가격_내림차순() {
		// given
		ProductSearchCondition condition = new ProductSearchCondition(null, null, null, "price", "desc");
		int page = 0;
		int size = 10;

		Brand brand = Brand.builder().id(1L).brandName("Brand").build();
		Product product = Product.builder().id(1L).brand(brand).name("Product").price(10000).build();
		Page<Product> productPage = new PageImpl<>(List.of(product));

		given(productQueryService.getProducts(eq(condition), any(Pageable.class)))
			.willReturn(productPage);

		// when
		Page<ProductResDto> result = productFacadeService.getProducts(condition, page, size);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().name()).isEqualTo("Product");

		verify(productQueryService).getProducts(eq(condition), argThat(pageable -> {
			Sort sort = pageable.getSort();
			Sort.Order order = sort.getOrderFor("price");
			return order != null && order.isDescending();
		}));
	}
}

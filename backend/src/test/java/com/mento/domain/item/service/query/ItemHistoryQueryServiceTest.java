package com.mento.domain.item.service.query;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mento.domain.brand.entity.Brand;
import com.mento.domain.item.entity.ItemHistory;
import com.mento.domain.item.enums.ItemHistoryAction;
import com.mento.domain.item.repository.ItemHistoryRepository;
import com.mento.domain.product.entity.Product;
import com.mento.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemHistoryQueryService 단위 테스트")
class ItemHistoryQueryServiceTest {

	@Mock
	private ItemHistoryRepository itemHistoryRepository;

	@InjectMocks
	private ItemHistoryQueryServiceImpl itemHistoryQueryService;

	private User testUser;
	private Brand testBrand;
	private Product testProduct;

	@BeforeEach
	void setUp() {
		testUser = createUser(1L, "테스트유저", "user@test.com");
		testBrand = createBrand(1L, "테스트브랜드");
		testProduct = createProduct(1L, "테스트상품", testBrand, "스킨케어", 90);
	}

	private User createUser(final Long id, final String name, final String email) {
		return User.builder()
			.id(id)
			.name(name)
			.email(email)
			.build();
	}

	// ===== Test Fixture Methods =====

	private Brand createBrand(final Long id, final String brandName) {
		return Brand.builder()
			.id(id)
			.brandName(brandName)
			.build();
	}

	private Product createProduct(
		final Long id,
		final String name,
		final Brand brand,
		final String categoryMedium,
		final Integer defaultUsageDays
	) {
		return Product.builder()
			.id(id)
			.name(name)
			.brand(brand)
			.categoryMedium(categoryMedium)
			.defaultUsageDays(defaultUsageDays)
			.build();
	}

	private ItemHistory createItemHistory(
		final Long id,
		final User user,
		final Product product,
		final ItemHistoryAction action
	) {
		return ItemHistory.builder()
			.id(id)
			.user(user)
			.product(product)
			.actionType(action)
			.build();
	}

	@Nested
	@DisplayName("히스토리 목록 조회 테스트")
	class FindAllByUserIdWithFiltersTest {

		@Test
		@DisplayName("전체 히스토리 조회 성공")
		void 전체_히스토리_조회_성공() {
			// given
			Long userId = testUser.getId();
			Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

			ItemHistory history1 = createItemHistory(1L, testUser, testProduct, ItemHistoryAction.CREATED);
			ItemHistory history2 = createItemHistory(2L, testUser, testProduct, ItemHistoryAction.EXPIRED);
			List<ItemHistory> histories = List.of(history1, history2);
			Page<ItemHistory> historyPage = new PageImpl<>(histories, pageable, 2);

			given(itemHistoryRepository.findAllByUserIdWithFilters(
				eq(userId), isNull(), isNull(), isNull(), eq(pageable)
			)).willReturn(historyPage);

			// when
			Page<ItemHistory> result = itemHistoryQueryService.findAllByUserIdWithFilters(
				userId, null, null, null, pageable
			);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getTotalElements()).isEqualTo(2);

			then(itemHistoryRepository).should(times(1))
				.findAllByUserIdWithFilters(eq(userId), isNull(), isNull(), isNull(), eq(pageable));
		}

		@Test
		@DisplayName("특정 상품 필터링 조회 성공")
		void 특정_상품_필터링_조회_성공() {
			// given
			Long userId = testUser.getId();
			Long productId = testProduct.getId();
			Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

			ItemHistory history = createItemHistory(1L, testUser, testProduct, ItemHistoryAction.CREATED);
			List<ItemHistory> histories = List.of(history);
			Page<ItemHistory> historyPage = new PageImpl<>(histories, pageable, 1);

			given(itemHistoryRepository.findAllByUserIdWithFilters(
				eq(userId), eq(productId), isNull(), isNull(), eq(pageable)
			)).willReturn(historyPage);

			// when
			Page<ItemHistory> result = itemHistoryQueryService.findAllByUserIdWithFilters(
				userId, productId, null, null, pageable
			);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent().get(0).getProduct().getId()).isEqualTo(productId);

			then(itemHistoryRepository).should(times(1))
				.findAllByUserIdWithFilters(eq(userId), eq(productId), isNull(), isNull(), eq(pageable));
		}

		@Test
		@DisplayName("날짜 범위 필터링 조회 성공")
		void 날짜_범위_필터링_조회_성공() {
			// given
			Long userId = testUser.getId();
			LocalDate startDate = LocalDate.of(2026, 1, 1);
			LocalDate endDate = LocalDate.of(2026, 1, 31);
			Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

			ItemHistory history = createItemHistory(1L, testUser, testProduct, ItemHistoryAction.CREATED);
			List<ItemHistory> histories = List.of(history);
			Page<ItemHistory> historyPage = new PageImpl<>(histories, pageable, 1);

			given(itemHistoryRepository.findAllByUserIdWithFilters(
				eq(userId), isNull(), eq(startDate), eq(endDate), eq(pageable)
			)).willReturn(historyPage);

			// when
			Page<ItemHistory> result = itemHistoryQueryService.findAllByUserIdWithFilters(
				userId, null, startDate, endDate, pageable
			);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContent()).hasSize(1);

			then(itemHistoryRepository).should(times(1))
				.findAllByUserIdWithFilters(eq(userId), isNull(), eq(startDate), eq(endDate), eq(pageable));
		}

		@Test
		@DisplayName("빈 결과 조회 성공")
		void 빈_결과_조회_성공() {
			// given
			Long userId = testUser.getId();
			Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
			Page<ItemHistory> emptyPage = new PageImpl<>(List.of(), pageable, 0);

			given(itemHistoryRepository.findAllByUserIdWithFilters(
				eq(userId), isNull(), isNull(), isNull(), eq(pageable)
			)).willReturn(emptyPage);

			// when
			Page<ItemHistory> result = itemHistoryQueryService.findAllByUserIdWithFilters(
				userId, null, null, null, pageable
			);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContent()).isEmpty();
			assertThat(result.getTotalElements()).isZero();
		}
	}
}

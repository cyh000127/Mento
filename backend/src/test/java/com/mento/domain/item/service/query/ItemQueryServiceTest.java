package com.mento.domain.item.service.query;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

import com.mento.common.error.ErrorCode;
import com.mento.domain.brand.entity.Brand;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.enums.ItemCategory;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.exception.ItemException;
import com.mento.domain.item.repository.ItemRepository;
import com.mento.domain.product.entity.Product;
import com.mento.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemQueryService 단위 테스트")
class ItemQueryServiceTest {

	@Mock
	private ItemRepository itemRepository;

	@InjectMocks
	private ItemQueryServiceImpl itemQueryService;

	private User testUser;
	private Product testProduct;

	@BeforeEach
	void setUp() {
		testUser = createUser(1L, "테스트유저", "test@example.com");
		final Brand testBrand = createBrand(1L, "테스트브랜드");
		testProduct = createProduct(1L, "테스트상품", testBrand, "스킨케어", 90);
	}

	private User createUser(final Long id, final String name, final String email) {
		return User.builder()
			.id(id)
			.name(name)
			.email(email)
			.build();
	}

	private Brand createBrand(final Long id, final String brandName) {
		return Brand.builder()
			.id(id)
			.brandName(brandName)
			.build();
	}

	// ===== Test Fixture Methods =====

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

	private Item createItem(
		final Long id,
		final User user,
		final Product product,
		final ItemStatus status,
		final Boolean isFavorite,
		final Integer purchaseCount
	) {
		return Item.builder()
			.id(id)
			.user(user)
			.product(product)
			.status(status)
			.isFavorite(isFavorite)
			.purchaseCount(purchaseCount)
			.purchaseDate(LocalDate.now())
			.expectedExpiryDate(LocalDate.now().plusDays(30))
			.build();
	}

	@Nested
	@DisplayName("아이템 ID로 조회 테스트")
	class FindByIdTest {

		@Test
		@DisplayName("아이템 조회 성공")
		void 아이템_조회_성공() {
			// given
			Long itemId = 1L;
			Item item = createItem(itemId, testUser, testProduct, ItemStatus.OWNED, false, 1);

			given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

			// when
			Item result = itemQueryService.findById(itemId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(itemId);
			assertThat(result.getUser()).isEqualTo(testUser);
			assertThat(result.getProduct()).isEqualTo(testProduct);
			assertThat(result.getStatus()).isEqualTo(ItemStatus.OWNED);
			then(itemRepository).should(times(1)).findById(itemId);
		}

		@Test
		@DisplayName("아이템 조회 실패 - 존재하지 않는 아이템")
		void 아이템_조회_실패_존재하지_않는_아이템() {
			// given
			Long itemId = 999L;
			given(itemRepository.findById(itemId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> itemQueryService.findById(itemId))
				.isInstanceOf(ItemException.class)
				.hasMessageContaining(ErrorCode.ITEM_NOT_FOUND.getMessage());

			then(itemRepository).should(times(1)).findById(itemId);
		}

		@Test
		@DisplayName("여러 아이템 개별 조회 성공")
		void 여러_아이템_개별_조회_성공() {
			// given
			Item item1 = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);
			Item item2 = createItem(2L, testUser, testProduct, ItemStatus.OWNED, true, 2);
			Item item3 = createItem(3L, testUser, testProduct, ItemStatus.UNAVAILABLE, false, 3);

			given(itemRepository.findById(1L)).willReturn(Optional.of(item1));
			given(itemRepository.findById(2L)).willReturn(Optional.of(item2));
			given(itemRepository.findById(3L)).willReturn(Optional.of(item3));

			// when
			Item result1 = itemQueryService.findById(1L);
			Item result2 = itemQueryService.findById(2L);
			Item result3 = itemQueryService.findById(3L);

			// then
			assertThat(result1.getId()).isEqualTo(1L);
			assertThat(result1.getIsFavorite()).isFalse();

			assertThat(result2.getId()).isEqualTo(2L);
			assertThat(result2.getIsFavorite()).isTrue();

			assertThat(result3.getId()).isEqualTo(3L);
			assertThat(result3.getStatus()).isEqualTo(ItemStatus.UNAVAILABLE);

			then(itemRepository).should(times(3)).findById(anyLong());
		}
	}

	@Nested
	@DisplayName("아이템 목록 조회 테스트")
	class FindAllByUserIdWithFiltersTest {

		@Test
		@DisplayName("필터 없이 전체 조회 성공")
		void 필터_없이_전체_조회_성공() {
			// given
			Long userId = 1L;
			Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

			Item item1 = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);
			Item item2 = createItem(2L, testUser, testProduct, ItemStatus.OWNED, true, 2);
			List<Item> items = List.of(item1, item2);
			Page<Item> itemPage = new PageImpl<>(items, pageable, 2);

			given(itemRepository.findAllByUserIdWithFilters(userId, null, null, null, pageable))
				.willReturn(itemPage);

			// when
			Page<Item> result = itemQueryService.findAllByUserIdWithFilters(
				userId, null, null, null, pageable
			);

			// then
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getTotalElements()).isEqualTo(2);
			assertThat(result.getContent()).containsExactly(item1, item2);
			then(itemRepository).should(times(1))
				.findAllByUserIdWithFilters(userId, null, null, null, pageable);
		}

		@Test
		@DisplayName("상태 필터링 조회 성공")
		void 상태_필터링_조회_성공() {
			// given
			Long userId = 1L;
			ItemStatus status = ItemStatus.OWNED;
			Pageable pageable = PageRequest.of(0, 10);

			Item item1 = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);
			Item item2 = createItem(2L, testUser, testProduct, ItemStatus.OWNED, true, 2);
			List<Item> items = List.of(item1, item2);
			Page<Item> itemPage = new PageImpl<>(items, pageable, 2);

			given(itemRepository.findAllByUserIdWithFilters(userId, status, null, null, pageable))
				.willReturn(itemPage);

			// when
			Page<Item> result = itemQueryService.findAllByUserIdWithFilters(
				userId, status, null, null, pageable
			);

			// then
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getContent()).allMatch(item -> item.getStatus() == ItemStatus.OWNED);
			then(itemRepository).should(times(1))
				.findAllByUserIdWithFilters(userId, status, null, null, pageable);
		}

		@Test
		@DisplayName("카테고리 필터링 조회 성공")
		void 카테고리_필터링_조회_성공() {
			// given
			Long userId = 1L;
			ItemCategory category = ItemCategory.SKIN;
			Pageable pageable = PageRequest.of(0, 10);

			Item item1 = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);
			List<Item> items = List.of(item1);
			Page<Item> itemPage = new PageImpl<>(items, pageable, 1);

			given(itemRepository.findAllByUserIdWithFilters(userId, null, "스킨케어", null, pageable))
				.willReturn(itemPage);

			// when
			Page<Item> result = itemQueryService.findAllByUserIdWithFilters(
				userId, null, category, null, pageable
			);

			// then
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent().getFirst().getProduct().getCategoryMedium()).isEqualTo("스킨케어");
			then(itemRepository).should(times(1))
				.findAllByUserIdWithFilters(userId, null, "스킨케어", null, pageable);
		}

		@Test
		@DisplayName("즐겨찾기 필터링 조회 성공")
		void 즐겨찾기_필터링_조회_성공() {
			// given
			Long userId = 1L;
			Boolean isFavorite = true;
			Pageable pageable = PageRequest.of(0, 10);

			Item item1 = createItem(1L, testUser, testProduct, ItemStatus.OWNED, true, 1);
			Item item2 = createItem(2L, testUser, testProduct, ItemStatus.OWNED, true, 2);
			List<Item> items = List.of(item1, item2);
			Page<Item> itemPage = new PageImpl<>(items, pageable, 2);

			given(itemRepository.findAllByUserIdWithFilters(userId, null, null, isFavorite, pageable))
				.willReturn(itemPage);

			// when
			Page<Item> result = itemQueryService.findAllByUserIdWithFilters(
				userId, null, null, isFavorite, pageable
			);

			// then
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getContent()).allMatch(Item::getIsFavorite);
			then(itemRepository).should(times(1))
				.findAllByUserIdWithFilters(userId, null, null, isFavorite, pageable);
		}

		@Test
		@DisplayName("복합 필터링 조회 성공")
		void 복합_필터링_조회_성공() {
			// given
			Long userId = 1L;
			ItemStatus status = ItemStatus.OWNED;
			ItemCategory category = ItemCategory.SKIN;
			Boolean isFavorite = true;
			Pageable pageable = PageRequest.of(0, 10);

			Item item1 = createItem(1L, testUser, testProduct, ItemStatus.OWNED, true, 1);
			List<Item> items = List.of(item1);
			Page<Item> itemPage = new PageImpl<>(items, pageable, 1);

			given(itemRepository.findAllByUserIdWithFilters(
				userId, status, "스킨케어", isFavorite, pageable
			)).willReturn(itemPage);

			// when
			Page<Item> result = itemQueryService.findAllByUserIdWithFilters(
				userId, status, category, isFavorite, pageable
			);

			// then
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent().get(0).getStatus()).isEqualTo(ItemStatus.OWNED);
			assertThat(result.getContent().get(0).getIsFavorite()).isTrue();
			then(itemRepository).should(times(1))
				.findAllByUserIdWithFilters(userId, status, "스킨케어", isFavorite, pageable);
		}

		@Test
		@DisplayName("조회 결과 없음")
		void 조회_결과_없음() {
			// given
			Long userId = 1L;
			Pageable pageable = PageRequest.of(0, 10);
			Page<Item> emptyPage = new PageImpl<>(List.of(), pageable, 0);

			given(itemRepository.findAllByUserIdWithFilters(userId, null, null, null, pageable))
				.willReturn(emptyPage);

			// when
			Page<Item> result = itemQueryService.findAllByUserIdWithFilters(
				userId, null, null, null, pageable
			);

			// then
			assertThat(result.getContent()).isEmpty();
			assertThat(result.getTotalElements()).isZero();
			then(itemRepository).should(times(1))
				.findAllByUserIdWithFilters(userId, null, null, null, pageable);
		}

		@Test
		@DisplayName("페이징 처리 성공")
		void 페이징_처리_성공() {
			// given
			Long userId = 1L;
			Pageable pageable = PageRequest.of(1, 5);

			Item item6 = createItem(6L, testUser, testProduct, ItemStatus.OWNED, false, 1);
			Item item7 = createItem(7L, testUser, testProduct, ItemStatus.OWNED, false, 1);
			List<Item> items = List.of(item6, item7);
			Page<Item> itemPage = new PageImpl<>(items, pageable, 12);

			given(itemRepository.findAllByUserIdWithFilters(userId, null, null, null, pageable))
				.willReturn(itemPage);

			// when
			Page<Item> result = itemQueryService.findAllByUserIdWithFilters(
				userId, null, null, null, pageable
			);

			// then
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getTotalElements()).isEqualTo(12);
			assertThat(result.getTotalPages()).isEqualTo(3);
			assertThat(result.getNumber()).isEqualTo(1);
			then(itemRepository).should(times(1))
				.findAllByUserIdWithFilters(userId, null, null, null, pageable);
		}
	}
}

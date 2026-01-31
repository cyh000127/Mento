package com.mento.domain.item.service.command;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.common.error.ErrorCode;
import com.mento.domain.brand.entity.Brand;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.exception.ItemException;
import com.mento.domain.item.repository.ItemRepository;
import com.mento.domain.product.entity.Product;
import com.mento.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemCommandService 단위 테스트")
class ItemCommandServiceTest {

	@Mock
	private ItemRepository itemRepository;

	@InjectMocks
	private ItemCommandServiceImpl itemCommandService;

	private User testUser;
	private Brand testBrand;
	private Product testProduct;

	@BeforeEach
	void setUp() {
		testUser = createUser(1L, "테스트유저", "test@example.com");
		testBrand = createBrand(1L, "테스트브랜드");
		testProduct = createProduct(1L, "테스트상품", testBrand, 90);
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

	private Product createProduct(final Long id, final String name, final Brand brand, final Integer defaultUsageDays) {
		return Product.builder()
			.id(id)
			.name(name)
			.brand(brand)
			.defaultUsageDays(defaultUsageDays)
			.build();
	}

	private Item createItem(final Long id, final User user, final Product product,
		final ItemStatus status, final Boolean isFavorite, final Integer purchaseCount) {
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

	// ===== Test Fixture Methods =====

	private Item createItemWithCustomDates(final Long id, final User user, final Product product,
		final ItemStatus status, final Boolean isFavorite, final Integer purchaseCount,
		final LocalDate purchaseDate, final LocalDate expectedExpiryDate) {
		return Item.builder()
			.id(id)
			.user(user)
			.product(product)
			.status(status)
			.isFavorite(isFavorite)
			.purchaseCount(purchaseCount)
			.purchaseDate(purchaseDate)
			.expectedExpiryDate(expectedExpiryDate)
			.build();
	}

	@Nested
	@DisplayName("아이템 저장 테스트")
	class SaveItemTest {

		@Test
		@DisplayName("기본 아이템 저장 성공")
		void 아이템_저장_성공() {
			// given
			Item item = createItem(null, testUser, testProduct, ItemStatus.OWNED, false, 1);
			Item savedItem = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);

			given(itemRepository.save(item)).willReturn(savedItem);

			// when
			Item result = itemCommandService.saveItem(item);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(1L);
			assertThat(result.getUser()).isEqualTo(testUser);
			assertThat(result.getProduct()).isEqualTo(testProduct);
			assertThat(result.getStatus()).isEqualTo(ItemStatus.OWNED);
			assertThat(result.getIsFavorite()).isFalse();
			assertThat(result.getPurchaseCount()).isEqualTo(1);
			then(itemRepository).should(times(1)).save(item);
		}

		@Test
		@DisplayName("즐겨찾기 설정된 아이템 저장 성공")
		void 아이템_저장_성공_즐겨찾기_설정() {
			// given
			Item item = createItem(null, testUser, testProduct, ItemStatus.OWNED, true, 1);
			Item savedItem = createItem(1L, testUser, testProduct, ItemStatus.OWNED, true, 1);

			given(itemRepository.save(item)).willReturn(savedItem);

			// when
			Item result = itemCommandService.saveItem(item);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(1L);
			assertThat(result.getIsFavorite()).isTrue();
			then(itemRepository).should(times(1)).save(item);
		}

		@Test
		@DisplayName("구매횟수가 여러번인 아이템 저장 성공")
		void 아이템_저장_성공_구매횟수_여러번() {
			// given
			Item item = createItem(null, testUser, testProduct, ItemStatus.OWNED, false, 5);
			Item savedItem = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 5);

			given(itemRepository.save(item)).willReturn(savedItem);

			// when
			Item result = itemCommandService.saveItem(item);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getPurchaseCount()).isEqualTo(5);
			then(itemRepository).should(times(1)).save(item);
		}
	}

	@Nested
	@DisplayName("아이템 상태 업데이트 테스트")
	class UpdateStatusTest {

		@Test
		@DisplayName("UNAVAILABLE로 상태 변경 성공")
		void 아이템_상태_업데이트_UNAVAILABLE로_변경_성공() {
			// given
			Item item = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);

			// when
			item.updateStatus(ItemStatus.UNAVAILABLE);

			// then
			assertThat(item.getStatus()).isEqualTo(ItemStatus.UNAVAILABLE);
			assertThat(item.getPurchaseCount()).isEqualTo(1);
		}

		@Test
		@DisplayName("OVER_DATED로 상태 변경 성공")
		void 아이템_상태_업데이트_OVER_DATED로_변경_성공() {
			// given
			Item item = createItemWithCustomDates(
				1L, testUser, testProduct, ItemStatus.OWNED, false, 1,
				LocalDate.now().minusDays(100), LocalDate.now().minusDays(10)
			);

			// when
			item.updateStatus(ItemStatus.OVER_DATED);

			// then
			assertThat(item.getStatus()).isEqualTo(ItemStatus.OVER_DATED);
		}

		@Test
		@DisplayName("OWNED로 상태 변경 시 구매 정보 갱신")
		void 아이템_상태_업데이트_OWNED로_변경_구매정보_갱신() {
			// given
			LocalDate oldPurchaseDate = LocalDate.now().minusDays(100);
			LocalDate oldExpiryDate = LocalDate.now().minusDays(10);

			Item item = createItemWithCustomDates(
				1L, testUser, testProduct, ItemStatus.PURCHASING, false, 3,
				oldPurchaseDate, oldExpiryDate
			);

			// when
			item.updateStatus(ItemStatus.OWNED);

			// then
			assertThat(item.getStatus()).isEqualTo(ItemStatus.OWNED);
			assertThat(item.getPurchaseCount()).isEqualTo(4);
			assertThat(item.getPurchaseDate()).isEqualTo(LocalDate.now());
			assertThat(item.getExpectedExpiryDate()).isEqualTo(LocalDate.now().plusDays(90));
			assertThat(item.getPurchaseDate()).isNotEqualTo(oldPurchaseDate);
			assertThat(item.getExpectedExpiryDate()).isNotEqualTo(oldExpiryDate);
		}

		@Test
		@DisplayName("null 상태로 업데이트 시 예외 발생")
		void 아이템_상태_업데이트_실패_null_상태() {
			// given
			Item item = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);

			// when & then
			assertThatThrownBy(() -> item.updateStatus(null))
				.isInstanceOf(ItemException.class)
				.hasMessageContaining(ErrorCode.BAD_REQUEST.getMessage());
		}
	}

	@Nested
	@DisplayName("엔티티 관계 할당 테스트")
	class AssignRelationTest {

		@Test
		@DisplayName("User 할당 성공")
		void 아이템_User_할당_성공() {
			// given
			Item item = Item.builder()
				.product(testProduct)
				.status(ItemStatus.OWNED)
				.isFavorite(false)
				.purchaseCount(1)
				.purchaseDate(LocalDate.now())
				.expectedExpiryDate(LocalDate.now().plusDays(30))
				.build();

			// when
			item.assignUser(testUser);

			// then
			assertThat(item.getUser()).isEqualTo(testUser);
		}

		@Test
		@DisplayName("null User 할당 시 예외 발생")
		void 아이템_User_할당_실패_null_사용자() {
			// given
			Item item = Item.builder()
				.product(testProduct)
				.status(ItemStatus.OWNED)
				.isFavorite(false)
				.purchaseCount(1)
				.purchaseDate(LocalDate.now())
				.expectedExpiryDate(LocalDate.now().plusDays(30))
				.build();

			// when & then
			assertThatThrownBy(() -> item.assignUser(null))
				.isInstanceOf(ItemException.class)
				.hasMessageContaining(ErrorCode.MISSING_USER.getMessage());
		}

		@Test
		@DisplayName("Product 할당 성공")
		void 아이템_Product_할당_성공() {
			// given
			Item item = Item.builder()
				.user(testUser)
				.status(ItemStatus.OWNED)
				.isFavorite(false)
				.purchaseCount(1)
				.purchaseDate(LocalDate.now())
				.expectedExpiryDate(LocalDate.now().plusDays(30))
				.build();

			// when
			item.assignProduct(testProduct);

			// then
			assertThat(item.getProduct()).isEqualTo(testProduct);
		}

		@Test
		@DisplayName("null Product 할당 시 예외 발생")
		void 아이템_Product_할당_실패_null_상품() {
			// given
			Item item = Item.builder()
				.user(testUser)
				.status(ItemStatus.OWNED)
				.isFavorite(false)
				.purchaseCount(1)
				.purchaseDate(LocalDate.now())
				.expectedExpiryDate(LocalDate.now().plusDays(30))
				.build();

			// when & then
			assertThatThrownBy(() -> item.assignProduct(null))
				.isInstanceOf(ItemException.class)
				.hasMessageContaining(ErrorCode.MISSING_PRODUCT.getMessage());
		}
	}

	@Nested
	@DisplayName("즐겨찾기 토글 테스트")
	class ToggleFavoriteTest {

		@Test
		@DisplayName("false에서 true로 변경")
		void 즐겨찾기_토글_false에서_true로_변경() {
			// given
			Item item = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);

			// when
			item.toggleFavorite();

			// then
			assertThat(item.getIsFavorite()).isTrue();
		}

		@Test
		@DisplayName("true에서 false로 변경")
		void 즐겨찾기_토글_true에서_false로_변경() {
			// given
			Item item = createItem(1L, testUser, testProduct, ItemStatus.OWNED, true, 1);

			// when
			item.toggleFavorite();

			// then
			assertThat(item.getIsFavorite()).isFalse();
		}

		@Test
		@DisplayName("여러 번 호출 시 올바른 토글 동작")
		void 즐겨찾기_토글_여러번_호출() {
			// given
			Item item = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);

			// when
			item.toggleFavorite(); // false -> true
			item.toggleFavorite(); // true -> false
			item.toggleFavorite(); // false -> true

			// then
			assertThat(item.getIsFavorite()).isTrue();
		}
	}
}

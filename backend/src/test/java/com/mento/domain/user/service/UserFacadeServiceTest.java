package com.mento.domain.user.service;

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

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.brand.entity.Brand;
import com.mento.domain.item.dto.common.ItemInfoResDto;
import com.mento.domain.item.dto.response.ItemPageResDto;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.enums.SortType;
import com.mento.domain.item.factory.ItemFactory;
import com.mento.domain.item.factory.ItemHistoryFactory;
import com.mento.domain.item.service.command.ItemCommandService;
import com.mento.domain.item.service.command.ItemHistoryCommandService;
import com.mento.domain.item.service.query.ItemQueryService;
import com.mento.domain.item.validator.ItemValidator;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.service.query.ProductQueryService;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.service.query.ReservationQueryService;
import com.mento.domain.user.dto.request.MentorAddItemReqDto;
import com.mento.domain.user.dto.request.UserItemsReqDto;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserFacadeService 단위 테스트")
class UserFacadeServiceTest {

	@Mock
	private UserQueryServiceImpl userQueryService;

	@Mock
	private ItemQueryService itemQueryService;

	@Mock
	private ItemValidator itemValidator;

	@Mock
	private ReservationQueryService reservationQueryService;

	@Mock
	private ItemCommandService itemCommandService;

	@Mock
	private ItemFactory itemFactory;

	@Mock
	private ProductQueryService productQueryService;

	@Mock
	private ItemHistoryCommandService itemHistoryCommandService;

	@Mock
	private ItemHistoryFactory itemHistoryFactory;

	@InjectMocks
	private UserFacadeService userFacadeService;

	private AuthenticatedUser mentorAuthUser;
	private User testUser;
	private User testMentor;
	private Brand testBrand;
	private Product testProduct;
	private Reservation testReservation;

	@BeforeEach
	void setUp() {
		mentorAuthUser = createAuthenticatedUser(1L, "mentor@test.com", "ROLE_MENTOR");
		testUser = createUser(2L, "테스트유저", "user@test.com");
		testMentor = createMentorUser(1L, "mentor@test.com", "테스트멘토");
		testBrand = createBrand(1L, "테스트브랜드");
		testProduct = createProduct(1L, "테스트상품", testBrand, "스킨케어", 90);
		testReservation = createReservation(1L, testUser, testMentor);
	}

	@Test
	@DisplayName("회원_조회_성공_본인")
	void 회원_조회_성공_본인() {
		// given
		Long userId = 1L;
		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(userId)
			.role("USER")
			.build();

		User user = User.builder()
			.id(userId)
			.name("홍길동")
			.email("hong@example.com")
			.build();

		given(userQueryService.findById(userId)).willReturn(user);

		// when
		UserResDto result = userFacadeService.getUser(userId, authUser);

		// then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(userId);
	}

	@Test
	@DisplayName("회원_조회_성공_멘토")
	void 회원_조회_성공_멘토() {
		// given
		Long targetId = 2L;
		Long mentoId = 1L;
		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(mentoId)
			.role("MENTO")
			.build();

		User user = User.builder()
			.id(targetId)
			.name("홍길동")
			.build();

		given(userQueryService.findById(targetId)).willReturn(user);

		// when
		UserResDto result = userFacadeService.getUser(targetId, authUser);

		// then
		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("회원_조회_성공_관리자")
	void 회원_조회_성공_관리자() {
		// given
		Long targetId = 2L;
		Long adminId = 1L;
		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(adminId)
			.role("ADMIN")
			.build();

		User user = User.builder()
			.id(targetId)
			.name("홍길동")
			.build();

		given(userQueryService.findById(targetId)).willReturn(user);

		// when
		UserResDto result = userFacadeService.getUser(targetId, authUser);

		// then
		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("회원_조회_실패_타인")
	void 회원_조회_실패_타인() {
		// given
		Long targetId = 2L;
		Long userId = 1L;
		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(userId)
			.role("USER")
			.build();

		// when & then
		assertThatThrownBy(() -> userFacadeService.getUser(targetId, authUser))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
	}

	private AuthenticatedUser createAuthenticatedUser(final Long id, final String email, final String role) {
		return AuthenticatedUser.builder()
			.id(id)
			.email(email)
			.role(role)
			.build();
	}

	// ===== Test Fixture Methods =====

	private User createUser(final Long id, final String name, final String email) {
		return User.builder()
			.id(id)
			.name(name)
			.email(email)
			.build();
	}

	private User createMentorUser(final Long id, final String email, final String name) {
		MentorType mentorType = MentorType.builder()
			.typeName("스킨케어")
			.build();
		return User.builder()
			.id(id)
			.email(email)
			.name(name)
			.password("password")
			.kakaoId("mentor_kakao")
			.role(Role.MENTOR)
			.mentorType(mentorType)
			.build();
	}

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

	private Reservation createReservation(final Long id, final User user, final User mentor) {
		return Reservation.builder()
			.id(id)
			.user(user)
			.mentor(mentor)
			.build();
	}

	@Nested
	@DisplayName("고객 인벤토리 목록 조회 테스트")
	class GetAllItemsByUserIdTest {

		@Test
		@DisplayName("멘토가 고객 아이템 목록 조회 성공")
		void 멘토가_고객_아이템_목록_조회_성공() {
			// given
			Long userId = testUser.getId();
			UserItemsReqDto reqDto = UserItemsReqDto.builder()
				.reservationId(1L)
				.status(null)
				.category(null)
				.isFavorite(null)
				.sortType(SortType.LATEST)
				.page(0)
				.size(10)
				.build();

			Item item1 = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);
			Item item2 = createItem(2L, testUser, testProduct, ItemStatus.OWNED, true, 2);
			List<Item> items = List.of(item1, item2);

			Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
			Page<Item> itemPage = new PageImpl<>(items, pageable, 2);

			given(reservationQueryService.findById(1L)).willReturn(testReservation);
			given(itemQueryService.findAllByUserIdWithFilters(
				eq(userId), isNull(), isNull(), isNull(), any(Pageable.class)
			)).willReturn(itemPage);

			// when
			Page<ItemPageResDto> result = userFacadeService.getAllItemsByUserId(mentorAuthUser, userId, reqDto);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getTotalElements()).isEqualTo(2);

			then(reservationQueryService).should(times(1)).findById(1L);
			then(itemValidator).should(times(1))
				.validateMentorAccess(mentorAuthUser, testReservation, userId);
			then(itemQueryService).should(times(1))
				.findAllByUserIdWithFilters(eq(userId), isNull(), isNull(), isNull(), any(Pageable.class));
		}

		@Test
		@DisplayName("상태 필터링으로 아이템 조회 성공")
		void 상태_필터링으로_아이템_조회_성공() {
			// given
			Long userId = testUser.getId();
			UserItemsReqDto reqDto = UserItemsReqDto.builder()
				.reservationId(1L)
				.status(ItemStatus.OWNED)
				.category(null)
				.isFavorite(null)
				.sortType(SortType.LATEST)
				.page(0)
				.size(10)
				.build();

			Item item1 = createItem(1L, testUser, testProduct, ItemStatus.OWNED, false, 1);
			List<Item> items = List.of(item1);

			Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
			Page<Item> itemPage = new PageImpl<>(items, pageable, 1);

			given(reservationQueryService.findById(1L)).willReturn(testReservation);
			given(itemQueryService.findAllByUserIdWithFilters(
				eq(userId), eq(ItemStatus.OWNED), isNull(), isNull(), any(Pageable.class)
			)).willReturn(itemPage);

			// when
			Page<ItemPageResDto> result = userFacadeService.getAllItemsByUserId(mentorAuthUser, userId, reqDto);

			// then
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent()).allMatch(item -> item.status() == ItemStatus.OWNED);

			then(itemQueryService).should(times(1))
				.findAllByUserIdWithFilters(eq(userId), eq(ItemStatus.OWNED), isNull(), isNull(),
					any(Pageable.class));
		}

		@Test
		@DisplayName("즐겨찾기 필터링으로 아이템 조회 성공")
		void 즐겨찾기_필터링으로_아이템_조회_성공() {
			// given
			Long userId = testUser.getId();
			UserItemsReqDto reqDto = UserItemsReqDto.builder()
				.reservationId(1L)
				.status(null)
				.category(null)
				.isFavorite(true)
				.sortType(SortType.LATEST)
				.page(0)
				.size(10)
				.build();

			Item item1 = createItem(1L, testUser, testProduct, ItemStatus.OWNED, true, 1);
			Item item2 = createItem(2L, testUser, testProduct, ItemStatus.OWNED, true, 2);
			List<Item> items = List.of(item1, item2);

			Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
			Page<Item> itemPage = new PageImpl<>(items, pageable, 2);

			given(reservationQueryService.findById(1L)).willReturn(testReservation);
			given(itemQueryService.findAllByUserIdWithFilters(
				eq(userId), isNull(), isNull(), eq(true), any(Pageable.class)
			)).willReturn(itemPage);

			// when
			Page<ItemPageResDto> result = userFacadeService.getAllItemsByUserId(mentorAuthUser, userId, reqDto);

			// then
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getContent()).allMatch(ItemPageResDto::isFavorite);

			then(itemQueryService).should(times(1))
				.findAllByUserIdWithFilters(eq(userId), isNull(), isNull(), eq(true), any(Pageable.class));
		}

		@Test
		@DisplayName("멘토 권한 검증 실패 시 예외 발생")
		void 멘토_권한_검증_실패_시_예외_발생() {
			// given
			Long userId = testUser.getId();
			UserItemsReqDto reqDto = UserItemsReqDto.builder()
				.reservationId(1L)
				.status(null)
				.category(null)
				.isFavorite(null)
				.sortType(SortType.LATEST)
				.page(0)
				.size(10)
				.build();

			given(reservationQueryService.findById(1L)).willReturn(testReservation);
			willThrow(new BusinessException(ErrorCode.ACCESS_DENIED))
				.given(itemValidator).validateMentorAccess(mentorAuthUser, testReservation, userId);

			// when & then
			assertThatThrownBy(
				() -> userFacadeService.getAllItemsByUserId(mentorAuthUser, userId, reqDto))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());

			then(reservationQueryService).should(times(1)).findById(1L);
			then(itemValidator).should(times(1))
				.validateMentorAccess(mentorAuthUser, testReservation, userId);
			then(itemQueryService).should(never())
				.findAllByUserIdWithFilters(anyLong(), any(), any(), any(), any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("멘토가 고객에게 아이템 추가 테스트")
	class AddItemToUserTest {

		@Test
		@DisplayName("멘토가 고객에게 아이템 추가 성공")
		void 멘토가_고객에게_아이템_추가_성공() {
			// given
			Long userId = testUser.getId();
			MentorAddItemReqDto reqDto = MentorAddItemReqDto.builder()
				.productId(1L)
				.reservationId(1L)
				.build();

			Item createdItem = createItem(1L, testUser, testProduct, ItemStatus.RECOMMENDED, false, 1);

			given(reservationQueryService.findById(1L)).willReturn(testReservation);
			given(productQueryService.findById(1L)).willReturn(testProduct);
			given(userQueryService.findById(userId)).willReturn(testUser);
			given(itemFactory.createItem(testUser, testProduct, ItemStatus.RECOMMENDED)).willReturn(createdItem);
			given(itemCommandService.saveItem(createdItem)).willReturn(createdItem);

			// when
			ItemInfoResDto result = userFacadeService.addItemToUser(
				mentorAuthUser, userId, reqDto
			);

			// then
			assertThat(result).isNotNull();
			assertThat(result.id()).isEqualTo(1L);
			assertThat(result.status()).isEqualTo(ItemStatus.RECOMMENDED);

			then(reservationQueryService).should(times(1)).findById(1L);
			then(itemValidator).should(times(1))
				.validateMentorAccess(mentorAuthUser, testReservation, userId);
			then(productQueryService).should(times(1)).findById(1L);
			then(userQueryService).should(times(1)).findById(userId);
			then(itemFactory).should(times(1)).createItem(testUser, testProduct, ItemStatus.RECOMMENDED);
			then(itemCommandService).should(times(1)).saveItem(createdItem);
		}

		@Test
		@DisplayName("멘토 권한 검증 실패 시 예외 발생")
		void 멘토_권한_검증_실패_시_예외_발생() {
			// given
			Long userId = testUser.getId();
			MentorAddItemReqDto reqDto = MentorAddItemReqDto.builder()
				.productId(1L)
				.reservationId(1L)
				.build();

			given(reservationQueryService.findById(1L)).willReturn(testReservation);
			willThrow(new BusinessException(ErrorCode.ACCESS_DENIED))
				.given(itemValidator).validateMentorAccess(mentorAuthUser, testReservation, userId);

			// when & then
			assertThatThrownBy(
				() -> userFacadeService.addItemToUser(mentorAuthUser, userId, reqDto))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());

			then(reservationQueryService).should(times(1)).findById(1L);
			then(itemValidator).should(times(1))
				.validateMentorAccess(mentorAuthUser, testReservation, userId);
			then(productQueryService).should(never()).findById(anyLong());
			then(itemCommandService).should(never()).saveItem(any(Item.class));
		}

		@Test
		@DisplayName("존재하지 않는 상품 ID로 추가 시 예외 발생")
		void 존재하지_않는_상품_ID로_추가_시_예외_발생() {
			// given
			Long userId = testUser.getId();
			Long invalidProductId = 999L;
			MentorAddItemReqDto reqDto = MentorAddItemReqDto.builder()
				.productId(invalidProductId)
				.reservationId(1L)
				.build();

			given(reservationQueryService.findById(1L)).willReturn(testReservation);
			given(productQueryService.findById(invalidProductId))
				.willThrow(new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

			// when & then
			assertThatThrownBy(
				() -> userFacadeService.addItemToUser(mentorAuthUser, userId, reqDto))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());

			then(reservationQueryService).should(times(1)).findById(1L);
			then(itemValidator).should(times(1))
				.validateMentorAccess(mentorAuthUser, testReservation, userId);
			then(productQueryService).should(times(1)).findById(invalidProductId);
			then(itemCommandService).should(never()).saveItem(any(Item.class));
		}

		@Test
		@DisplayName("존재하지 않는 사용자 ID로 추가 시 예외 발생")
		void 존재하지_않는_사용자_ID로_추가_시_예외_발생() {
			// given
			Long invalidUserId = 999L;
			MentorAddItemReqDto reqDto = MentorAddItemReqDto.builder()
				.productId(1L)
				.reservationId(1L)
				.build();

			given(reservationQueryService.findById(1L)).willReturn(testReservation);
			given(productQueryService.findById(1L)).willReturn(testProduct);
			given(userQueryService.findById(invalidUserId))
				.willThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

			// when & then
			assertThatThrownBy(
				() -> userFacadeService.addItemToUser(mentorAuthUser, invalidUserId, reqDto))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

			then(reservationQueryService).should(times(1)).findById(1L);
			then(itemValidator).should(times(1))
				.validateMentorAccess(mentorAuthUser, testReservation, invalidUserId);
			then(productQueryService).should(times(1)).findById(1L);
			then(userQueryService).should(times(1)).findById(invalidUserId);
			then(itemCommandService).should(never()).saveItem(any(Item.class));
		}
	}
}

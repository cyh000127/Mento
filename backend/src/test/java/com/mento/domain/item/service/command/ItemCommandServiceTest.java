package com.mento.domain.item.service.command;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.domain.item.entity.Item;
import com.mento.domain.item.enums.ItemStatus;
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

	@Test
	@DisplayName("아이템_저장_성공")
	void 아이템_저장_성공() {
		// given
		User user = User.builder()
			.id(1L)
			.name("테스트유저")
			.email("test@example.com")
			.build();

		Product product = Product.builder()
			.id(1L)
			.name("테스트상품")
			.build();

		Item item = Item.builder()
			.user(user)
			.product(product)
			.status(ItemStatus.OWNED)
			.isFavorite(false)
			.purchaseCount(1)
			.purchaseDate(LocalDate.now())
			.expectedExpiryDate(LocalDate.now().plusDays(30))
			.build();

		Item savedItem = Item.builder()
			.id(1L)
			.user(user)
			.product(product)
			.status(ItemStatus.OWNED)
			.isFavorite(false)
			.purchaseCount(1)
			.purchaseDate(LocalDate.now())
			.expectedExpiryDate(LocalDate.now().plusDays(30))
			.build();

		given(itemRepository.save(item)).willReturn(savedItem);

		// when
		Item result = itemCommandService.saveItem(item);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getUser()).isEqualTo(user);
		assertThat(result.getProduct()).isEqualTo(product);
		assertThat(result.getStatus()).isEqualTo(ItemStatus.OWNED);
		assertThat(result.getIsFavorite()).isFalse();
		assertThat(result.getPurchaseCount()).isEqualTo(1);
		then(itemRepository).should(times(1)).save(item);
	}

	@Test
	@DisplayName("아이템_저장_성공_즐겨찾기_설정")
	void 아이템_저장_성공_즐겨찾기_설정() {
		// given
		User user = User.builder()
			.id(1L)
			.name("테스트유저")
			.email("test@example.com")
			.build();

		Product product = Product.builder()
			.id(1L)
			.name("테스트상품")
			.build();

		Item item = Item.builder()
			.user(user)
			.product(product)
			.status(ItemStatus.OWNED)
			.isFavorite(true)
			.purchaseCount(1)
			.purchaseDate(LocalDate.now())
			.expectedExpiryDate(LocalDate.now().plusDays(30))
			.build();

		Item savedItem = Item.builder()
			.id(1L)
			.user(user)
			.product(product)
			.status(ItemStatus.OWNED)
			.isFavorite(true)
			.purchaseCount(1)
			.purchaseDate(LocalDate.now())
			.expectedExpiryDate(LocalDate.now().plusDays(30))
			.build();

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
	@DisplayName("아이템_저장_성공_구매횟수_여러번")
	void 아이템_저장_성공_구매횟수_여러번() {
		// given
		User user = User.builder()
			.id(1L)
			.name("테스트유저")
			.email("test@example.com")
			.build();

		Product product = Product.builder()
			.id(1L)
			.name("테스트상품")
			.build();

		Item item = Item.builder()
			.user(user)
			.product(product)
			.status(ItemStatus.OWNED)
			.isFavorite(false)
			.purchaseCount(5)
			.purchaseDate(LocalDate.now())
			.expectedExpiryDate(LocalDate.now().plusDays(30))
			.build();

		Item savedItem = Item.builder()
			.id(1L)
			.user(user)
			.product(product)
			.status(ItemStatus.OWNED)
			.isFavorite(false)
			.purchaseCount(5)
			.purchaseDate(LocalDate.now())
			.expectedExpiryDate(LocalDate.now().plusDays(30))
			.build();

		given(itemRepository.save(item)).willReturn(savedItem);

		// when
		Item result = itemCommandService.saveItem(item);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getPurchaseCount()).isEqualTo(5);
		then(itemRepository).should(times(1)).save(item);
	}
}

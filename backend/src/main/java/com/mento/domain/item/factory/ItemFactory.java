package com.mento.domain.item.factory;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.mento.domain.item.entity.Item;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.product.entity.Product;
import com.mento.domain.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ItemFactory {

	public Item createItem(final User user, final Product product, final ItemStatus status) {
		LocalDate today = LocalDate.now();
		LocalDate expectedExpiryDate = today.plusDays(product.getDefaultUsageDays());

		Item item = Item.builder()
			.status(status)
			.isFavorite(false)
			.purchaseCount(1)
			.purchaseDate(today)
			.expectedExpiryDate(expectedExpiryDate)
			.build();

		item.assignProduct(product);
		item.assignUser(user);

		log.info("[Item] 생성 완료 {userId: {}, productId: {}, expectedExpiryDate: {}}",
			user.getId(), product.getId(), expectedExpiryDate);

		return item;
	}
}

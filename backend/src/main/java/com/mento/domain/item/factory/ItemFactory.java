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

	public Item createItem(final User user, final Product product) {
		Item item = Item.builder()
			.status(ItemStatus.OWNED)
			.isFavorite(false)
			.purchaseCount(1)
			.purchaseDate(LocalDate.now())
			.expectedExpiryDate(LocalDate.now())
			.build();

		item.assignProduct(product);
		item.assignUser(user);

		return item;
	}
}

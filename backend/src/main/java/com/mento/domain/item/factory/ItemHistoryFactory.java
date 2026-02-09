package com.mento.domain.item.factory;

import org.springframework.stereotype.Component;

import com.mento.domain.item.entity.Item;
import com.mento.domain.item.entity.ItemHistory;
import com.mento.domain.item.enums.ItemHistoryAction;
import com.mento.domain.product.entity.Product;
import com.mento.domain.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ItemHistoryFactory {

	public ItemHistory createHistory(final User user, final Product product, final ItemHistoryAction action) {
		ItemHistory history = ItemHistory.builder()
			.actionType(action)
			.build();

		history.assignUser(user);
		history.assignProduct(product);

		log.debug("[ItemHistoryFactory] 히스토리 생성 {userId: {}, productId: {}, action: {}}",
			user.getId(), product.getId(), action);

		return history;
	}

	public ItemHistory createHistoryFromItem(final Item item, final ItemHistoryAction action) {
		return createHistory(item.getUser(), item.getProduct(), action);
	}
}

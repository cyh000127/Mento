package com.mento.domain.item.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mento.domain.item.entity.Item;
import com.mento.domain.item.enums.ItemCategory;
import com.mento.domain.item.enums.ItemStatus;

public interface ItemQueryService {

	Item findById(Long itemId);

	Page<Item> findAllByUserIdWithFilters(
		Long userId,
		ItemStatus status,
		ItemCategory category,
		Boolean isFavorite,
		Pageable pageable
	);
}

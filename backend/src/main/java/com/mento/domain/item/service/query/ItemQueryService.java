package com.mento.domain.item.service.query;

import java.time.LocalDate;
import java.util.List;

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

	List<Item> findOverdueItems(LocalDate today);
}

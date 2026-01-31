package com.mento.domain.item.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.enums.ItemCategory;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.exception.ItemException;
import com.mento.domain.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemQueryServiceImpl implements ItemQueryService {

	private final ItemRepository itemRepository;

	@Override
	public Item findById(final Long itemId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND));
		log.info("[Item] 아이템 상세 조회 완료 {itemID : {}}", item.getId());
		return item;
	}

	@Override
	public Page<Item> findAllByUserIdWithFilters(
		final Long userId,
		final ItemStatus status,
		final ItemCategory category,
		final Boolean isFavorite,
		final Pageable pageable
	) {
		String categoryValue = category != null ? category.getDescription() : null;
		Page<Item> items = itemRepository.findAllByUserIdWithFilters(
			userId, status, categoryValue, isFavorite, pageable
		);
		log.info("[Item] 사용자 아이템 목록 조회 완료 {userId: {}, totalElements: {}}", userId, items.getTotalElements());
		return items;
	}
}

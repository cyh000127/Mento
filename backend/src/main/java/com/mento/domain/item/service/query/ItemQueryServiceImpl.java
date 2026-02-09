package com.mento.domain.item.service.query;

import java.time.LocalDate;
import java.util.List;

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
		log.info("[Item] 아이템 조회 완료 {itemID : {}}", item.getId());
		return item;
	}

	@Override
	public Item findByIdWithDetail(final Long itemId) {
		Item item = itemRepository.findWithDetailsById(itemId)
			.orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND));
		log.info("[Item] 아이템 상세 조회 완료 {itemID : {}}", item.getId());
		return item;
	}

	@Override
	public Page<Item> findAllByUserId(final Long userId, final Pageable pageable) {
		Page<Item> items = itemRepository.findAllByUserId(userId, pageable);
		log.info("[Item] 사용자 아이템 목록 조회 - userId: {}, 조회된 아이템 수: {}, 전체 페이지: {}", userId, items.getContent().size(),
			items.getTotalPages());
		return items;
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

	@Override
	public List<Item> findOverdueItems(final LocalDate today) {
		return itemRepository.findOverdueItems(today);
	}

	@Override
	public List<Item> findItemsExpiringBetween(final LocalDate startDate, final LocalDate endDate) {
		List<Item> items = itemRepository.findItemsExpiringBetween(startDate, endDate);
		log.info("[Item] 만료 예정 아이템 조회 완료 {startDate: {}, endDate: {}, count: {}}",
			startDate, endDate, items.size());
		return items;
	}
}

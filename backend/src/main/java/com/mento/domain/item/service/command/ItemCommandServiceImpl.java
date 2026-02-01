package com.mento.domain.item.service.command;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.item.entity.Item;
import com.mento.domain.item.repository.ItemRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCommandServiceImpl implements ItemCommandService {

	private final ItemRepository itemRepository;

	@Override
	public Item saveItem(final Item item) {
		Item savedItem = itemRepository.save(item);
		log.info("[Item] 아이템 저장 성공 {userId: {}, itemId : {}}", item.getUser().getId(), item.getId());
		return savedItem;
	}

	@Override
	public int expireOverdueItems(final LocalDate today) {
		int updatedCount = itemRepository.updateOverdueItemsToExpired(today);
		log.info("[Item] 만료 아이템 상태 업데이트 완료 {count: {}}", updatedCount);
		return updatedCount;
	}
}

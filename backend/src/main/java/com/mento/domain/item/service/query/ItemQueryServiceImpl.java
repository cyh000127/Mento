package com.mento.domain.item.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.item.entity.Item;
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
}

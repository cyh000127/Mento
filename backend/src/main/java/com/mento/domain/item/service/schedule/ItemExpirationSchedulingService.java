package com.mento.domain.item.service.schedule;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.item.entity.Item;
import com.mento.domain.item.entity.ItemHistory;
import com.mento.domain.item.enums.ItemHistoryAction;
import com.mento.domain.item.factory.ItemHistoryFactory;
import com.mento.domain.item.service.command.ItemCommandService;
import com.mento.domain.item.service.command.ItemHistoryCommandService;
import com.mento.domain.item.service.query.ItemQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemExpirationSchedulingService {

	private final ItemHistoryCommandService itemHistoryCommandService;

	private final ItemQueryService itemQueryService;
	private final ItemCommandService itemCommandService;
	private final ItemHistoryFactory itemHistoryFactory;

	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void expireOverdueItems() {
		log.info("[ItemExpirationScheduler] 만료 아이템 처리 시작");

		LocalDate today = LocalDate.now();
		List<Item> overdueItems = itemQueryService.findOverdueItems(today);

		int updatedCount = itemCommandService.expireOverdueItems(today);

		for (Item item : overdueItems) {
			ItemHistory history = itemHistoryFactory.createHistoryFromItem(item, ItemHistoryAction.EXPIRED);
			itemHistoryCommandService.saveHistory(history);
		}

		log.info("[ItemExpirationScheduler] 만료 아이템 처리 완료 {count: {}}", updatedCount);
	}
}

package com.mento.domain.item.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.item.entity.ItemHistory;
import com.mento.domain.item.repository.ItemHistoryRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemHistoryCommandServiceImpl implements ItemHistoryCommandService {

	private final ItemHistoryRepository itemHistoryRepository;

	@Override
	public ItemHistory saveHistory(final ItemHistory history) {
		ItemHistory savedHistory = itemHistoryRepository.save(history);
		log.info("[ItemHistory] 히스토리 기록 완료 {userId: {}, productId: {}, action: {}}",
			history.getUser().getId(), history.getProduct().getId(), history.getActionType());
		return savedHistory;
	}
}

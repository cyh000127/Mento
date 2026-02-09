package com.mento.domain.item.service.query;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.item.entity.ItemHistory;
import com.mento.domain.item.repository.ItemHistoryRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class ItemHistoryQueryServiceImpl implements ItemHistoryQueryService {

	private final ItemHistoryRepository itemHistoryRepository;

	@Override
	public Page<ItemHistory> findAllByUserIdWithFilters(
		final Long userId,
		final Long productId,
		final LocalDate startDate,
		final LocalDate endDate,
		final Pageable pageable
	) {
		Page<ItemHistory> histories = itemHistoryRepository.findAllByUserIdWithFilters(
			userId, productId, startDate, endDate, pageable
		);
		log.info("[ItemHistory] 히스토리 조회 완료 {userId: {}, count: {}}", userId, histories.getTotalElements());
		return histories;
	}
}

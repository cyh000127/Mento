package com.mento.domain.item.service.query;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mento.domain.item.entity.ItemHistory;

public interface ItemHistoryQueryService {

	Page<ItemHistory> findAllByUserIdWithFilters(
		Long userId,
		Long productId,
		LocalDate startDate,
		LocalDate endDate,
		Pageable pageable
	);
}
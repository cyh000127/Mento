package com.mento.domain.item.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mento.domain.item.entity.ItemHistory;

public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {

	@Query("""
		SELECT ih FROM ItemHistory ih
		JOIN FETCH ih.product p
		JOIN FETCH p.brand b
		WHERE ih.user.id = :userId
		AND (:productId IS NULL OR ih.product.id = :productId)
		AND (:startDate IS NULL OR CAST(ih.createdAt AS LocalDate) >= :startDate)
		AND (:endDate IS NULL OR CAST(ih.createdAt AS LocalDate) <= :endDate)
		""")
	Page<ItemHistory> findAllByUserIdWithFilters(
		@Param("userId") Long userId,
		@Param("productId") Long productId,
		@Param("startDate") LocalDate startDate,
		@Param("endDate") LocalDate endDate,
		Pageable pageable
	);
}
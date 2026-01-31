package com.mento.domain.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mento.domain.item.entity.Item;
import com.mento.domain.item.enums.ItemStatus;

public interface ItemRepository extends JpaRepository<Item, Long> {

	@Query("""
		SELECT i FROM Item i
		JOIN FETCH i.product p
		JOIN FETCH p.brand b
		WHERE i.user.id = :userId
		AND i.deletedAt IS NULL
		AND (:status IS NULL OR i.status = :status)
		AND (:category IS NULL OR p.categoryMedium = :category)
		AND (:isFavorite IS NULL OR i.isFavorite = :isFavorite)
		""")
	Page<Item> findAllByUserIdWithFilters(
		@Param("userId") Long userId,
		@Param("status") ItemStatus status,
		@Param("category") String category,
		@Param("isFavorite") Boolean isFavorite,
		Pageable pageable
	);
}
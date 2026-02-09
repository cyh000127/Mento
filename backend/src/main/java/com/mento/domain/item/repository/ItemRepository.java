package com.mento.domain.item.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

	@Query("""
		SELECT i FROM Item i
		JOIN FETCH i.product p
		JOIN FETCH p.brand b
		WHERE i.user.id = :userId
		AND i.deletedAt IS NULL
		""")
	Page<Item> findAllByUserId(
		@Param("userId") Long userId,
		Pageable pageable
	);

	@Query("""
		SELECT i FROM Item i
		WHERE i.status != 'OVER_DATED'
		AND i.expectedExpiryDate < :today
		AND i.deletedAt IS NULL
		""")
	List<Item> findOverdueItems(@Param("today") LocalDate today);

	@Modifying
	@Query("""
		UPDATE Item i
		SET i.status = 'OVER_DATED'
		WHERE i.status != 'OVER_DATED'
		AND i.expectedExpiryDate < :today
		AND i.deletedAt IS NULL
		""")
	int updateOverdueItemsToExpired(@Param("today") LocalDate today);

	@Query("""
		SELECT i
		FROM Item i
		JOIN FETCH i.product
		JOIN FETCH i.user
		WHERE i.id = :itemId
		""")
	Optional<Item> findWithDetailsById(Long itemId);

	@Query("""
		SELECT i FROM Item i
		JOIN FETCH i.user
		WHERE i.expectedExpiryDate BETWEEN :startDate AND :endDate
		AND i.status = 'OWNED'
		AND i.deletedAt IS NULL
		ORDER BY i.expectedExpiryDate ASC
		""")
	List<Item> findItemsExpiringBetween(
		@Param("startDate") LocalDate startDate,
		@Param("endDate") LocalDate endDate
	);
}
package com.mento.domain.item.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.exception.ItemException;
import com.mento.domain.product.entity.Product;
import com.mento.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "items")
public class Item extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ItemStatus status = ItemStatus.OWNED;

	@Column(name = "is_favorite", nullable = false)
	private Boolean isFavorite;

	@Builder.Default
	@Column(name = "purchase_count", nullable = false)
	private Integer purchaseCount = 0;

	@Column(name = "purchase_date", nullable = false)
	private LocalDate purchaseDate;

	@Column(name = "expected_expiry_date", nullable = false)
	private LocalDate expectedExpiryDate;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public void updateStatus(final ItemStatus status) {
		if (status == null) {
			throw new ItemException(ErrorCode.BAD_REQUEST);
		}
		if (status == ItemStatus.OWNED) {
			updateStatusToOwn();
		}
		this.status = status;
	}

	public void toggleFavorite() {
		this.isFavorite = !this.isFavorite;
	}

	public void updateStatusToOwn() {
		this.purchaseCount++;
		this.purchaseDate = LocalDate.now();
		this.expectedExpiryDate = LocalDate.now().plusDays(this.getProduct().getDefaultUsageDays());
	}

	public void assignUser(final User user) {
		if (user == null) {
			throw new ItemException(ErrorCode.MISSING_USER);
		}
		this.user = user;
	}

	public void assignProduct(final Product product) {
		if (product == null) {
			throw new ItemException(ErrorCode.MISSING_PRODUCT);
		}
		this.product = product;
	}
}

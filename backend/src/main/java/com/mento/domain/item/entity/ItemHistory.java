package com.mento.domain.item.entity;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.domain.item.enums.ItemHistoryAction;
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

@Entity
@Table(name = "item_histories")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ItemHistoryAction actionType;

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
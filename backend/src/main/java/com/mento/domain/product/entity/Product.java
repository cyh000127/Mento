package com.mento.domain.product.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.domain.brand.entity.Brand;
import com.mento.domain.item.entity.UserItem;
import com.mento.domain.product.exception.ProductException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "brand_id", nullable = false)
	private Brand brand;

	@Builder.Default
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserItem> userItems = new ArrayList<>();

	@Column(name = "oliveyoung_goods_no", nullable = false, length = 50)
	private String oliveyoungGoodsNo;

	@Column(name = "category_medium", length = 100)
	private String categoryMedium;

	@Column(name = "category_small", length = 100)
	private String categorySmall;

	@Column(nullable = false, length = 500)
	private String name;

	@Column(length = 255)
	private String volume;

	@Column(length = 1000)
	private String description;

	@Column(columnDefinition = "TEXT")
	private String ingredients;

	@Column(nullable = false)
	@Builder.Default
	private Integer price = 0;

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	@Column(name = "product_url", length = 1000)
	private String productUrl;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "skin_types", columnDefinition = "json")
	private List<String> skinTypes;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "related_conditions", columnDefinition = "json")
	private List<String> relatedConditions;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "benefits", columnDefinition = "json")
	private List<String> benefits;

	@Column(name = "default_usage_days", nullable = false)
	@Builder.Default
	private Integer defaultUsageDays = 90;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public void assignUserItem(final UserItem userItem) {
		if (userItem == null) {
			throw new ProductException(ErrorCode.MISSING_USER_ITEM);
		}
		userItems.add(userItem);
		if (userItem.getProduct() != this) {
			userItem.assignProduct(this);
		}
	}
}

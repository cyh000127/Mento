package com.mento.domain.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.domain.inventory.entity.UserItem;
import com.mento.domain.product.exception.ProductException;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.user.exception.UserException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Reservation> reservations = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserItem> userItems = new ArrayList<>();

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 100)
	private String kakaoId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private Role role = Role.USER;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public void updateName(final String name) {
		this.name = name;
	}

	public void updateEmail(final String email) {
		this.email = email;
	}

	public void updateBirthDate(final LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public void withdraw() {
		this.deletedAt = LocalDateTime.now();
	}

	public void assignReservation(final Reservation reservation) {
		if (reservation == null) {
			throw new UserException(ErrorCode.MISSING_RESERVATION);
		}
		this.reservations.add(reservation);
		if (reservation.getUser() != this) {
			reservation.assignUser(this);
		}
	}

	public void assignUserItem(final UserItem userItem) {
		if (userItem == null) {
			throw new ProductException(ErrorCode.MISSING_USER_ITEM);
		}
		userItems.add(userItem);
		if (userItem.getUser() != this) {
			userItem.assignUser(this);
		}
	}
}

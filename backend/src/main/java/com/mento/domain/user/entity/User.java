package com.mento.domain.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.common.util.TimeUtils;
import com.mento.domain.item.entity.Item;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.product.exception.ProductException;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.skinanalysis.entity.SkinAnalysis;
import com.mento.domain.user.exception.UserException;

import jakarta.persistence.CascadeType;
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
	private List<Item> items = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SkinAnalysis> skinAnalyses = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mentor_type_id")
	private MentorType mentorType;

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
		this.deletedAt = TimeUtils.nowAsLocalDateTime();
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

	public void assignUserItem(final Item item) {
		if (item == null) {
			throw new ProductException(ErrorCode.MISSING_ITEM);
		}
		items.add(item);
		if (item.getUser() != this) {
			item.assignUser(this);
		}
	}

	public void assignMentorType(final MentorType mentorType) {
		if (mentorType == null) {
			throw new UserException(ErrorCode.MISSING_MENTOR_TYPE);
		}
		this.mentorType = mentorType;
	}

	public void assignSkinAnalysis(final SkinAnalysis skinAnalysis) {
		if (skinAnalysis == null) {
			throw new UserException(ErrorCode.MISSING_SKIN_ANALYSIS);
		}
		this.skinAnalyses.add(skinAnalysis);
		if (skinAnalysis.getUser() != this) {
			skinAnalysis.assignUser(this);
		}
	}

	public boolean isMentor() {
		return this.role == Role.MENTOR && this.mentorType != null;
	}

	public void validateMentorRole() {
		if (!isMentor()) {
			throw new UserException(ErrorCode.USER_NOT_MENTOR);
		}
	}

	public void validateMentorType(final MentorType expectedType) {
		validateMentorRole();
		if (!this.mentorType.equals(expectedType)) {
			throw new UserException(ErrorCode.INVALID_MENTOR_TYPE);
		}
	}
}

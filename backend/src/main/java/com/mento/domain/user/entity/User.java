package com.mento.domain.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.mento.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private Role role = Role.USER; // [USER, MENTO, ADMIN]

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

	public void withdraw() {
		this.deletedAt = LocalDateTime.now();
	}

}

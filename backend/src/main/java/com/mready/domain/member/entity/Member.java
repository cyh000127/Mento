package com.mready.domain.member.entity;

import com.mready.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member extends BaseEntity {

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

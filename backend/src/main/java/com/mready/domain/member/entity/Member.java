package com.mready.domain.member.entity;

import com.mready.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 현재는 PROVIDER_PROVIDERID로 변환 (ex. KAKAO_13859193)
	 **/
	@Column(unique = true)
	private String providerId;  // OAuth 고유 ID

	@Column(nullable = false)
	private String provider; // OAuth 가입 서비스 ( KAKAO, NAVER . . . )

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(length = 50)
	private String nickname; // 닉네임

	@Column(length = 500)
	private String profileImageUrl; // 프로필 이미지 URL


	@Column(length = 20)
	private String phoneNumber; // 전화번호

	@Column(length = 10)
	private String ageRange; // 연령대 (20~29)

	@Column(length = 4)
	private String birthday; // 생일 (MMDD)

	@Column(length = 4)
	private String birthYear; // 출생연도

	public void updateName(final String name) {
		this.name = name;
	}

	public void updateEmail(final String email) {
		this.email = email;
	}
}






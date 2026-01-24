package com.mready.domain.member.service.command;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mready.common.error.ErrorCode;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.exception.MemberException;
import com.mready.common.auth.redis.repository.RefreshTokenRepository;
import com.mready.domain.member.repository.MemberRepository;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberCommandService 단위 테스트")
class MemberCommandServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@InjectMocks
	private MemberCommandService memberCommandService;

	@Test
	@DisplayName("회원_탈퇴_성공_테스트")
	void 회원_탈퇴_성공_테스트() {
		// given
		Long memberId = 1L;
		Member member = Member.builder()
				.id(memberId)
				.name("탈퇴자")
				.email("leave@example.com")
				.build();
		// 초기 상태

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		willDoNothing().given(refreshTokenRepository).deleteById(String.valueOf(memberId));

		// when
		memberCommandService.withdraw(memberId);

		// then
		assertThat(member.getDeletedAt()).isNotNull(); // deletedAt이 설정되었는지 확인
		then(memberRepository).should(times(1)).findById(memberId);
		then(refreshTokenRepository).should(times(1)).deleteById(String.valueOf(memberId));
	}

	@Test
	@DisplayName("회원_탈퇴_실패_이미_탈퇴한_회원_테스트")
	void 회원_탈퇴_실패_이미_탈퇴한_회원_테스트() {
		// given
		Long memberId = 1L;
		Member member = Member.builder()
				.id(memberId)
				.name("탈퇴자")
				.email("leave@example.com")
				.build();
		member.withdraw(); // 이미 탈퇴 상태로 만듦

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

		// when & then
		assertThatThrownBy(() -> memberCommandService.withdraw(memberId))
				.isInstanceOf(MemberException.class)
				.hasMessageContaining(ErrorCode.ALREADY_WITHDRAWN.getMessage());

		then(memberRepository).should(times(1)).findById(memberId);
		then(refreshTokenRepository).should(never()).deleteById(anyString());
	}

	@Test
	@DisplayName("회원_탈퇴_실패_회원_없음_테스트")
	void 회원_탈퇴_실패_회원_없음_테스트() {
		// given
		Long memberId = 999L;
		given(memberRepository.findById(memberId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> memberCommandService.withdraw(memberId))
				.isInstanceOf(MemberException.class)
				.hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());

		then(refreshTokenRepository).should(never()).deleteById(anyString());
	}
}

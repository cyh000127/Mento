package com.mready.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mready.domain.member.dto.response.MemberResDto;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.service.command.MemberCommandService;
import com.mready.domain.member.service.query.MemberQueryService;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberFacadeService 단위 테스트")
class MemberFacadeServiceTest {

	@Mock
	private MemberCommandService memberCommandService;

	@Mock
	private MemberQueryService memberQueryService;

	@InjectMocks
	private MemberFacadeService memberFacadeService;

	@Test
	@DisplayName("회원_조회_및_DTO_변환_성공_테스트")
	void 회원_조회_및_DTO_변환_성공_테스트() {
		// given
		Long memberId = 1L;
		Member member = Member.builder()
				.id(memberId)
				.name("홍길동")
				.email("hong@example.com")
				.build();

		given(memberQueryService.findById(memberId)).willReturn(member);

		// when
		MemberResDto result = memberFacadeService.getMember(memberId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(memberId);
		assertThat(result.name()).isEqualTo("홍길동");
		assertThat(result.email()).isEqualTo("hong@example.com");

		then(memberQueryService).should(times(1)).findById(memberId);
	}
}

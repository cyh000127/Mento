package com.mready.domain.member.controller.query;

import com.mready.domain.member.dto.response.MemberResDto;
import com.mready.domain.member.service.MemberFacadeService;
import com.mready.domain.member.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberQueryControllerTest 단위 테스트")
class MemberQueryControllerTest {

        private MockMvc mockMvc;

        @Mock
        private MemberFacadeService memberFacadeService;

        @InjectMocks
        private MemberQueryController memberQueryController;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(memberQueryController)
                                .build();
        }

        @Test
        @DisplayName("회원 조회 성공 테스트 (200 OK)")
        void 회원_조회_테스트() throws Exception {
                // given
                Long memberId = 1L;
                MemberResDto response = MemberResDto.builder()
                                .id(memberId)
                                .name("Test User")
                                .email("test@example.com")
                                .birthDate(LocalDate.of(1990, 1, 1))
                                .role(Role.USER)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                given(memberFacadeService.getMember(memberId)).willReturn(response);

                // when & then
                mockMvc.perform(get("/api/v1/members/{memberId}", memberId))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers
                                                .jsonPath("$.data.id").value(1))
                                .andExpect(MockMvcResultMatchers
                                                .jsonPath("$.data.name").value("Test User"))
                                .andExpect(MockMvcResultMatchers
                                                .jsonPath("$.data.role").value("USER"));
        }
}

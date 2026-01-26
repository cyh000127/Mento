package com.mready.domain.consulting.service.command;

import com.mready.common.auth.principal.AuthenticatedUser;
import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.ConsultingException;
import com.mready.common.livekit.LiveKitManager;
import com.mready.domain.consulting.dto.LiveKitSessionResponse;
import com.mready.domain.reservation.entity.Reservation;
import com.mready.domain.reservation.repository.ReservationRepository;
import com.mready.domain.timetable.entity.Timetable;
import com.mready.domain.timetable.repository.TimetableRepository;
import com.mready.domain.user.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConsultingCommandServiceTest {

    @InjectMocks
    private ConsultingCommandService consultingCommandService;

    @Mock
    private LiveKitManager liveKitManager;

    @Mock
    private TimetableRepository timetableRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("상담_세션_멘토_입장")
    void 상담_세션_멘토_입장() {
        // Given
        Long timetableId = 1L;
        Long mentoId = 100L;
        Long userId = 200L;
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .id(mentoId)
                .email("mento@test.com")
                .role(Role.MENTO.name())
                .build();

        Timetable timetable = Timetable.builder()
                .scheduledDate(LocalDate.now())
                .scheduledTime(LocalTime.now().plusMinutes(5)) // 5분 후 시작
                .build();
        ReflectionTestUtils.setField(timetable, "id", timetableId); // ID 설정 필요시

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .mentoId(mentoId)
                .timetableId(timetableId)
                .build();

        given(timetableRepository.findById(timetableId)).willReturn(Optional.of(timetable));
        given(reservationRepository.findByTimetableId(timetableId)).willReturn(Optional.of(reservation));
        given(liveKitManager.createToken(anyString(), anyString(), anyString(), eq("MENTOR"), eq(true), anyLong()))
                .willReturn("mock_token");
        given(liveKitManager.getUrl()).willReturn("wss://test.url");

        // When
        LiveKitSessionResponse response = consultingCommandService.createSession(timetableId, authenticatedUser);

        // Then
        assertThat(response.roomToken()).isEqualTo("mock_token");
    }

    @Test
    @DisplayName("상담_세션_유저_입장")
    void 상담_세션_유저_입장() {
        // Given
        Long timetableId = 1L;
        Long mentoId = 100L;
        Long userId = 200L;
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .id(userId)
                .email("user@test.com")
                .role(Role.USER.name())
                .build();

        Timetable timetable = Timetable.builder()
                .scheduledDate(LocalDate.now())
                .scheduledTime(LocalTime.now().plusMinutes(5)) // 5분 후 시작
                .build();

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .mentoId(mentoId)
                .timetableId(timetableId)
                .build();

        given(timetableRepository.findById(timetableId)).willReturn(Optional.of(timetable));
        given(reservationRepository.findByTimetableId(timetableId)).willReturn(Optional.of(reservation));
        given(liveKitManager.createToken(anyString(), anyString(), anyString(), eq("CUSTOMER"), eq(false), anyLong()))
                .willReturn("mock_token_user");
        given(liveKitManager.getUrl()).willReturn("wss://test.url");

        // When
        LiveKitSessionResponse response = consultingCommandService.createSession(timetableId, authenticatedUser);

        // Then
        // Then
        assertThat(response.roomToken()).isEqualTo("mock_token_user");
        assertThat(response.participantRole()).isEqualTo("CUSTOMER");
        assertThat(response.livekitUrl()).isEqualTo("wss://test.url");
        assertThat(response.timetableId()).isEqualTo(timetableId);
    }

    @Test
    @DisplayName("실패_타임테이블_없음")
    void 실패_타임테이블_없음() {
        Long timetableId = 999L;
        AuthenticatedUser user = AuthenticatedUser.builder().id(1L).build();

        given(timetableRepository.findById(timetableId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> consultingCommandService.createSession(timetableId, user))
                .isInstanceOf(ConsultingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TIMETABLE_NOT_FOUND);
    }

    @Test
    @DisplayName("실패_입장_시간_아님")
    void 실패_입장_시간_아님() {
        // Given
        Long timetableId = 1L;
        AuthenticatedUser user = AuthenticatedUser.builder().id(1L).build();

        Timetable timetable = Timetable.builder()
                .scheduledDate(LocalDate.now().plusDays(1)) // 내일
                .scheduledTime(LocalTime.now())
                .build();

        given(timetableRepository.findById(timetableId)).willReturn(Optional.of(timetable));

        // When & Then
        assertThatThrownBy(() -> consultingCommandService.createSession(timetableId, user))
                .isInstanceOf(ConsultingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_STARTED_YET);
    }
    
    @Test
    @DisplayName("실패_종료된_상담")
    void 실패_종료된_상담() {
        // Given
        Long timetableId = 1L;
        AuthenticatedUser user = AuthenticatedUser.builder().id(1L).build();

        Timetable timetable = Timetable.builder()
                .scheduledDate(LocalDate.now().minusDays(1)) // 어제
                .scheduledTime(LocalTime.now())
                .build();

        given(timetableRepository.findById(timetableId)).willReturn(Optional.of(timetable));

        // When & Then
        assertThatThrownBy(() -> consultingCommandService.createSession(timetableId, user))
                .isInstanceOf(ConsultingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONSULTING_ENDED);
    }

    @Test
    @DisplayName("실패_권한_없는_사용자")
    void createSession_Fail_NotAuthorized() {
        // Given
        Long timetableId = 1L;
        Long mentoId = 100L;
        Long userId = 200L;

        AuthenticatedUser otherUser = AuthenticatedUser.builder().id(300L).role("USER").build(); 

        Timetable timetable = Timetable.builder()
                .scheduledDate(LocalDate.now())
                .scheduledTime(LocalTime.now())
                .build();

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .mentoId(mentoId)
                .build();

        given(timetableRepository.findById(timetableId)).willReturn(Optional.of(timetable));
        given(reservationRepository.findByTimetableId(timetableId)).willReturn(Optional.of(reservation));

        // When & Then
        assertThatThrownBy(() -> consultingCommandService.createSession(timetableId, otherUser))
                .isInstanceOf(ConsultingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_AUTHORIZED);
    }
}

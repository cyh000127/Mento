package com.mento.domain.consulting.service.facade;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.mento.domain.consulting.entity.Consulting;
import com.mento.domain.consulting.service.query.ConsultingQueryService;
import com.mento.domain.consulting.vo.ChatLogEntryVo;
import com.mento.domain.reservation.constants.LiveKitConstants;
import com.mento.domain.reservation.service.command.ReservationCommandService;

@ExtendWith(MockitoExtension.class)
class ConsultingFacadeServiceTest {

    @InjectMocks
    private ConsultingFacadeService consultingFacadeService;

    @Mock
    private ConsultingQueryService consultingQueryService;

    @Mock
    private ReservationCommandService reservationCommandService;
    @Mock
    private RedisTemplate<String, ChatLogEntryVo> chatLogEntryRedisTemplate;
    @Mock
    private ListOperations<String, ChatLogEntryVo> listOperations;

    @Test
    @DisplayName("상담 세션 종료 시 예약 상태를 완료로 변경한다")
    void endConsultingSession() {
        // given
        Long reservationId = 1L;
        String roomId = LiveKitConstants.ROOM_NAME_PREFIX + reservationId;
        List<ChatLogEntryVo> chatLogs = List.of(mock(ChatLogEntryVo.class));
        Consulting consulting = mock(Consulting.class);

        given(chatLogEntryRedisTemplate.opsForList()).willReturn(listOperations);
        given(listOperations.range(any(), eq(0L), eq(-1L))).willReturn(chatLogs);
        given(consultingQueryService.findByRoomId(roomId)).willReturn(consulting);

        // when
        consultingFacadeService.endConsultingSession(roomId);

        // then
        then(consulting).should(times(1)).updateChatLogs(chatLogs);
        then(chatLogEntryRedisTemplate).should(times(1)).delete(any(String.class));
        then(reservationCommandService).should(times(1)).completeReservation(reservationId);
    }
}

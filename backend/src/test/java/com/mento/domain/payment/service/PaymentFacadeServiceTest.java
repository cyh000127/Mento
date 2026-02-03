package com.mento.domain.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.PaymentException;
import com.mento.domain.consulting.entity.Consulting;
import com.mento.domain.consulting.factory.ConsultingFactory;
import com.mento.domain.consulting.service.command.ConsultingCommandServiceImpl;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.payment.dto.request.PaymentApproveReqDto;
import com.mento.domain.payment.dto.response.PaymentApproveResDto;
import com.mento.domain.payment.entity.Payment;
import com.mento.domain.payment.entity.PaymentMethod;
import com.mento.domain.payment.entity.PaymentStatus;
import com.mento.domain.payment.service.command.PaymentCommandService;
import com.mento.domain.payment.service.facade.PaymentFacadeService;
import com.mento.domain.payment.service.query.PaymentQueryService;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryServiceImpl;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeServiceTest {

	@Mock
	private PaymentCommandService paymentCommandService;

	@Mock
	private PaymentQueryService paymentQueryService;

	@Mock
	private UserQueryServiceImpl userQueryService;

	@Mock
	private ConsultingFactory consultingFactory;

	@Mock
	private ConsultingCommandServiceImpl consultingCommandService;

	@InjectMocks
	private PaymentFacadeService paymentFacadeService;

	private Long userId;
	private Long paymentId;
	private Payment payment;
	private Reservation reservation;

	@BeforeEach
	void setUp() {
		userId = 1L;
		paymentId = 100L;

		User user = User.builder()
			.name("테스트 사용자")
			.email("user@test.com")
			.build();
		ReflectionTestUtils.setField(user, "id", userId);

		MentorType mentorType = MentorType.builder()
			.typeName("스킨케어")
			.price(50000)
			.build();
		ReflectionTestUtils.setField(mentorType, "id", 1L);

		Timetable timetable = Timetable.builder()
			.scheduledDate(java.time.LocalDate.now())
			.scheduledTime(java.time.LocalTime.of(10, 0))
			.build();
		ReflectionTestUtils.setField(timetable, "id", 1L);

		TimetableSlot slot = TimetableSlot.builder()
			.timetable(timetable)
			.mentorType(mentorType)
			.build();
		ReflectionTestUtils.setField(slot, "id", 1L);

		User mentor = User.builder()
			.name("테스트 멘토")
			.email("mentor@test.com")
			.password("test1234")
			.kakaoId("mentor_kakao")
			.role(Role.MENTOR)
			.mentorType(mentorType)
			.build();
		ReflectionTestUtils.setField(mentor, "id", 10L);

		reservation = Reservation.builder()
			.user(user)
			.slot(slot)
			.mentor(mentor)
			.status(ReservationStatus.PENDING_PAYMENT)
			.build();
		ReflectionTestUtils.setField(reservation, "id", 1L);
		ReflectionTestUtils.setField(reservation, "createdAt", java.time.LocalDateTime.now());
		ReflectionTestUtils.setField(reservation, "updatedAt", java.time.LocalDateTime.now());

		payment = Payment.builder()
			.amount(50000L)
			.paymentMethod(PaymentMethod.KAKAO_PAY)
			.status(PaymentStatus.READY)
			.build();
		ReflectionTestUtils.setField(payment, "paymentId", paymentId);
		payment.assignReservation(reservation);
	}

	@Test
	@DisplayName("결제_승인_및_예약_확정_성공")
	void 결제_승인_및_예약_확정_성공() {
		// Given
		PaymentApproveReqDto request = PaymentApproveReqDto.builder()
			.paymentId(paymentId)
			.pgToken("test_pg_token")
			.build();

		PaymentApproveResDto approveResDto = PaymentApproveResDto.builder()
			.paymentId(paymentId)
			.paidAt(java.time.LocalDateTime.now())
			.build();

		Consulting consulting = Consulting.builder()
			.roomId("room_1")
			.build();

		given(paymentCommandService.approve(any(PaymentApproveReqDto.class), any(Long.class)))
			.willReturn(approveResDto);
		given(paymentQueryService.findById(paymentId))
			.willReturn(payment);
		given(userQueryService.findById(1L))
			.willReturn(reservation.getMentor());
		given(consultingFactory.createConsulting(anyLong()))
			.willReturn(consulting);

		// When
		ReservationDetailResDto result = paymentFacadeService.approvePaymentAndConfirmReservation(request, userId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.reservationId()).isEqualTo(1L);
		then(paymentCommandService).should(times(1)).approve(request, userId);
		then(paymentQueryService).should(times(1)).findById(paymentId);
		then(userQueryService).should(times(1)).findById(1L);
		then(consultingFactory).should(times(1)).createConsulting(anyLong());
		then(consultingCommandService).should(times(1)).saveDraftConsulting(consulting);
	}

	@Test
	@DisplayName("결제_승인_후_예약_정보_없음_실패")
	void 결제_승인_후_예약_정보_없음_실패() {
		// Given
		PaymentApproveReqDto request = PaymentApproveReqDto.builder()
			.paymentId(paymentId)
			.pgToken("test_pg_token")
			.build();

		PaymentApproveResDto approveResDto = PaymentApproveResDto.builder()
			.paymentId(paymentId)
			.paidAt(java.time.LocalDateTime.now())
			.build();

		Payment paymentWithoutReservation = Payment.builder()
			.amount(50000L)
			.paymentMethod(PaymentMethod.KAKAO_PAY)
			.status(PaymentStatus.READY)
			.build();
		ReflectionTestUtils.setField(paymentWithoutReservation, "paymentId", paymentId);

		given(paymentCommandService.approve(any(PaymentApproveReqDto.class), any(Long.class)))
			.willReturn(approveResDto);
		given(paymentQueryService.findById(paymentId))
			.willReturn(paymentWithoutReservation);

		// When & Then
		assertThatThrownBy(() ->
			paymentFacadeService.approvePaymentAndConfirmReservation(request, userId))
			.isInstanceOf(PaymentException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVATION_NOT_FOUND);
	}
}

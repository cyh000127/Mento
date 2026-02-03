package com.mento.domain.payment.service.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.PaymentException;
import com.mento.domain.consulting.entity.Consulting;
import com.mento.domain.consulting.factory.ConsultingFactory;
import com.mento.domain.consulting.service.command.ConsultingCommandService;
import com.mento.domain.payment.dto.request.PaymentApproveReqDto;
import com.mento.domain.payment.dto.request.PaymentReadyReqDto;
import com.mento.domain.payment.dto.response.PaymentApproveResDto;
import com.mento.domain.payment.dto.response.PaymentInfoDto;
import com.mento.domain.payment.dto.response.PaymentReadyResDto;
import com.mento.domain.payment.entity.Payment;
import com.mento.domain.payment.service.command.PaymentCommandService;
import com.mento.domain.payment.service.query.PaymentQueryService;
import com.mento.domain.reservation.converter.ReservationConverter;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryServiceImpl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentFacadeService {

	private final PaymentCommandService paymentCommandService;
	private final PaymentQueryService paymentQueryService;

	private final ConsultingCommandService consultingCommandService;
	private final ConsultingFactory consultingFactory;
	private final UserQueryServiceImpl userQueryService;

	@Transactional
	public PaymentReadyResDto preparePayment(final PaymentReadyReqDto request, final Long userId) {
		return paymentCommandService.ready(request, userId);
	}

	@Transactional
	public PaymentApproveResDto approvePayment(final PaymentApproveReqDto request, final Long userId) {
		return paymentCommandService.approve(request, userId);
	}

	public PaymentInfoDto findPaymentById(final Long paymentId) {
		return paymentQueryService.findPaymentById(paymentId);
	}

	@Transactional
	public ReservationDetailResDto approvePaymentAndConfirmReservation(
		final PaymentApproveReqDto request,
		final Long userId
	) {
		paymentCommandService.approve(request, userId);

		Payment payment = paymentQueryService.findById(request.paymentId());
		Reservation reservation = payment.getReservation();

		if (reservation == null) {
			throw new PaymentException(ErrorCode.RESERVATION_NOT_FOUND);
		}

		//테스트용 활성화 추후 수정 예정
		User mentor = userQueryService.findById(1L);

		reservation.getSlot().increaseCapacity();
		reservation.assignMentor(mentor);
		reservation.confirm();

		log.info("[Payment] 결제 승인 및 예약 확정 완료 {paymentId: {}, reservationId: {}, mentorId: {}}",
			payment.getPaymentId(), reservation.getId(), mentor.getId());

		Consulting consulting = consultingFactory.createConsulting(reservation.getId());
		consultingCommandService.saveDraftConsulting(consulting);

		return ReservationConverter.toReservationDetailResDto(reservation);
	}
}

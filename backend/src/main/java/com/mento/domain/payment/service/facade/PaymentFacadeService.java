package com.mento.domain.payment.service.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.PaymentException;
import com.mento.domain.mentor.entity.Mentor;
import com.mento.domain.mentor.service.query.MentorQueryService;
import com.mento.domain.payment.dto.request.PaymentApproveReqDto;
import com.mento.domain.payment.dto.request.PaymentReadyReqDto;
import com.mento.domain.payment.dto.response.PaymentApproveResDto;
import com.mento.domain.payment.dto.response.PaymentReadyResDto;
import com.mento.domain.payment.dto.response.PaymentResDto;
import com.mento.domain.payment.entity.Payment;
import com.mento.domain.payment.service.command.PaymentCommandService;
import com.mento.domain.payment.service.query.PaymentQueryService;
import com.mento.domain.reservation.converter.ReservationConverter;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.entity.Reservation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentFacadeService {

	private final PaymentCommandService paymentCommandService;
	private final PaymentQueryService paymentQueryService;
	private final MentorQueryService mentorQueryService;

	@Transactional
	public PaymentReadyResDto preparePayment(final PaymentReadyReqDto request, final Long userId) {
		return paymentCommandService.ready(request, userId);
	}

	@Transactional
	public PaymentApproveResDto approvePayment(final PaymentApproveReqDto request, final Long userId) {
		return paymentCommandService.approve(request, userId);
	}

	public PaymentResDto findPaymentById(final Long paymentId) {
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

		Mentor randomMentor = mentorQueryService.findRandomMentorByTypeId(
			reservation.getSlot().getMentorType().getId()
		);

		reservation.getSlot().increaseCapacity();
		reservation.assignMentor(randomMentor);
		reservation.confirm();

		log.info("[Payment] 결제 승인 및 예약 확정 완료 {paymentId: {}, reservationId: {}, mentorId: {}}",
			payment.getPaymentId(), reservation.getId(), randomMentor.getId());

		return ReservationConverter.toReservationDetailResDto(reservation);
	}
}

package com.mento.domain.payment.service.command;

import com.mento.domain.payment.dto.request.PaymentApproveReqDto;
import com.mento.domain.payment.dto.request.PaymentReadyReqDto;
import com.mento.domain.payment.dto.response.PaymentApproveResDto;
import com.mento.domain.payment.dto.response.PaymentReadyResDto;
import com.mento.domain.payment.entity.Payment;

public interface PaymentCommandService {

	PaymentReadyResDto ready(PaymentReadyReqDto request, Long userId);

	PaymentApproveResDto approve(PaymentApproveReqDto request, Long userId);

	Payment save(Payment payment);
}

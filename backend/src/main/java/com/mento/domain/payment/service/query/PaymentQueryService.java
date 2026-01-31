package com.mento.domain.payment.service.query;

import com.mento.domain.payment.dto.response.PaymentResDto;
import com.mento.domain.payment.entity.Payment;

public interface PaymentQueryService {

	Payment findById(Long id);

	PaymentResDto findPaymentById(Long id);
}

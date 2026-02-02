package com.mento.domain.payment.service.query;

import com.mento.domain.payment.dto.response.PaymentInfoDto;
import com.mento.domain.payment.entity.Payment;

public interface PaymentQueryService {

	Payment findById(Long id);

	PaymentInfoDto findPaymentById(Long id);
}

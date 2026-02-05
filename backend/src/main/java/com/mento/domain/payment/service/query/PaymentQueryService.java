package com.mento.domain.payment.service.query;

import com.mento.domain.payment.entity.Payment;

public interface PaymentQueryService {

	Payment findById(Long id);

	Payment findDetailsById(Long id);
}

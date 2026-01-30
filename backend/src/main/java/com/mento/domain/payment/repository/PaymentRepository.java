package com.mento.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

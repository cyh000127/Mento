package com.mento.domain.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mento.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	@Query("""
		SELECT p
		FROM Payment p
		JOIN FETCH p.reservation r
		JOIN FETCH r.slot s
		JOIN FETCH s.mentorType mt
		WHERE p.id =:id
		""")
	Optional<Payment> findDetailsById(Long id);
}

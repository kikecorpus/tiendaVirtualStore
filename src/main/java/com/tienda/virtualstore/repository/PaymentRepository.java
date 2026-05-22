package com.tienda.virtualstore.repository;

import com.tienda.virtualstore.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    // Para idempotencia del webhook
    boolean existsByMpPaymentId(String mpPaymentId);

    Optional<Payment> findByMpPreferenceId(String mpPreferenceId);
}
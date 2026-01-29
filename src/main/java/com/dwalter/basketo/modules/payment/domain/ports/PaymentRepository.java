package com.dwalter.basketo.modules.payment.domain.ports;

import com.dwalter.basketo.modules.payment.domain.model.Payment;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(UUID id);
}

package com.dwalter.basketo.modules.payment.infrastructure.adapters;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

interface SpringDataPaymentRepository extends JpaRepository<PaymentJpaEntity, UUID> {
    java.util.Optional<PaymentJpaEntity> findByOrderId(UUID orderId);
}

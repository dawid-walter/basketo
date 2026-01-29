package com.dwalter.basketo.modules.payment.infrastructure.adapters;

import com.dwalter.basketo.modules.payment.domain.model.Payment;
import com.dwalter.basketo.modules.payment.domain.ports.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class JpaPaymentRepositoryAdapter implements PaymentRepository {
    private final SpringDataPaymentRepository repository;
    private final Clock clock;

    @Override
    public void save(Payment payment) {
        // If payment is new, we use its creation time. If updating, preserve it.
        // But Payment domain object doesn't expose createdAt via getter currently (unless added).
        // For simplicity in this adapter, we might use clock for new ones.
        // Ideally Payment.getCreatedAt() should be used.
        // Let's assume we need to add getCreatedAt() to Payment or use clock here for persistence timestamp.
        repository.save(new PaymentJpaEntity(
            payment.getId(),
            payment.getOrderId(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getStatus(),
            payment.getCreatedAt()
        ));
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return repository.findById(id).map(e ->
                Payment.restore(e.getId(), e.getOrderId(), e.getAmount(), e.getCurrency(), e.getStatus(), e.getCreatedAt())
        );
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId).map(e ->
                Payment.restore(e.getId(), e.getOrderId(), e.getAmount(), e.getCurrency(), e.getStatus(), e.getCreatedAt())
        );
    }
}

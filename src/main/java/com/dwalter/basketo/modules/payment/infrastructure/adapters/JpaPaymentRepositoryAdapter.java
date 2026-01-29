package com.dwalter.basketo.modules.payment.infrastructure.adapters;

import com.dwalter.basketo.modules.payment.domain.model.Payment;
import com.dwalter.basketo.modules.payment.domain.ports.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class JpaPaymentRepositoryAdapter implements PaymentRepository {
    private final SpringDataPaymentRepository repository;

    @Override
    public void save(Payment payment) {
        repository.save(new PaymentJpaEntity(
            payment.getId(),
            payment.getOrderId(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getStatus(),
            java.time.Instant.now()
        ));
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId).map(e ->
                Payment.restore(e.getId(), e.getOrderId(), e.getAmount(), e.getCurrency(), e.getStatus())
        );
    }

    private java.time.Instant now() {
}
package com.dwalter.basketo.modules.payment.domain.events;

import com.dwalter.basketo.shared.domain.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(
        UUID eventId,
        UUID paymentId,
        UUID orderId,
        Instant occurredAt
) implements DomainEvent {
    public PaymentCompletedEvent(UUID paymentId, UUID orderId) {
        this(UUID.randomUUID(), paymentId, orderId, Instant.now());
    }
}

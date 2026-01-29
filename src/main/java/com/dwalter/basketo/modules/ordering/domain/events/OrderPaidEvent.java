package com.dwalter.basketo.modules.ordering.domain.events;

import com.dwalter.basketo.shared.domain.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record OrderPaidEvent(
        UUID eventId,
        UUID orderId,
        Instant occurredAt
) implements DomainEvent {
    public OrderPaidEvent(UUID orderId) {
        this(UUID.randomUUID(), orderId, Instant.now());
    }
    
    // Constructor with clock support if needed, but keeping simple for now
}

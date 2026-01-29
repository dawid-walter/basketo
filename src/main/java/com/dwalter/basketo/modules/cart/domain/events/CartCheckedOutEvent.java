package com.dwalter.basketo.modules.cart.domain.events;

import com.dwalter.basketo.shared.domain.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record CartCheckedOutEvent(
        UUID eventId,
        UUID cartId,
        UUID orderId,
        Instant occurredAt
) implements DomainEvent {
    public CartCheckedOutEvent(UUID cartId, UUID orderId) {
        this(UUID.randomUUID(), cartId, orderId, Instant.now());
    }
}

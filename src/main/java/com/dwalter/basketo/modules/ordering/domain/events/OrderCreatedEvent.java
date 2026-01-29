package com.dwalter.basketo.modules.ordering.domain.events;

import com.dwalter.basketo.modules.ordering.domain.model.OrderItem;
import com.dwalter.basketo.shared.domain.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String userEmail,
        BigDecimal totalAmount,
        String currency,
        List<OrderItem> items,
        Instant occurredAt
) implements DomainEvent {
    public OrderCreatedEvent(UUID orderId, String userEmail, BigDecimal totalAmount, String currency, List<OrderItem> items) {
        this(UUID.randomUUID(), orderId, userEmail, totalAmount, currency, items, Instant.now());
    }
}

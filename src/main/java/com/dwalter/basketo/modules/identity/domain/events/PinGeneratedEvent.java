package com.dwalter.basketo.modules.identity.domain.events;

import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record PinGeneratedEvent(UUID eventId, Email email, String pin, Instant occurredAt) implements DomainEvent {
    public PinGeneratedEvent(Email email, String pin) {
        this(UUID.randomUUID(), email, pin, Instant.now());
    }
}

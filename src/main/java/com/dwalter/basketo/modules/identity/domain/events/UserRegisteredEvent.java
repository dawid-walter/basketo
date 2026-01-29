package com.dwalter.basketo.modules.identity.domain.events;

import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(UUID eventId, UUID userId, Email email, Instant occurredAt) implements DomainEvent {
    public UserRegisteredEvent(UUID userId, Email email) {
        this(UUID.randomUUID(), userId, email, Instant.now());
    }
}

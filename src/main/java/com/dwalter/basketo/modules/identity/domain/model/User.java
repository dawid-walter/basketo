package com.dwalter.basketo.modules.identity.domain.model;

import com.dwalter.basketo.modules.identity.domain.events.PinGeneratedEvent;
import com.dwalter.basketo.modules.identity.domain.events.UserRegisteredEvent;
import com.dwalter.basketo.modules.identity.domain.ports.PinHasher;
import com.dwalter.basketo.shared.domain.AggregateRoot;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class User extends AggregateRoot {
    // ... fields ...
    private final UUID id;
    private final Email email;
    private String hashedPin;
    private Instant pinExpiresAt;

    private User(UUID id, Email email) {
        this.id = id;
        this.email = email;
    }

    public static User register(Email email, Clock clock) {
        User user = new User(UUID.randomUUID(), email);
        user.registerEvent(new UserRegisteredEvent(UUID.randomUUID(), user.id, user.email, Instant.now(clock)));
        return user;
    }

    public void generateNewPin(PinHasher hasher, Clock clock) {
        OneTimePin pin = OneTimePin.generate(15, clock);
        this.hashedPin = hasher.hash(pin.code());
        this.pinExpiresAt = pin.expiresAt();
        registerEvent(new PinGeneratedEvent(UUID.randomUUID(), this.email, pin.code(), Instant.now(clock)));
    }

    public boolean verifyPin(String pinCode, PinHasher hasher, Clock clock) {
        if (hashedPin == null || pinExpiresAt == null) return false;
        if (Instant.now(clock).isAfter(pinExpiresAt)) return false;
        return hasher.matches(pinCode, hashedPin);
    }
// ...

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getHashedPin() {
        return hashedPin;
    }

    public Instant getPinExpiresAt() {
        return pinExpiresAt;
    }

    // Reconstruction from DB
    public static User restore(UUID id, Email email, String hashedPin, Instant expiresAt) {
        User user = new User(id, email);
        user.hashedPin = hashedPin;
        user.pinExpiresAt = expiresAt;
        return user;
    }
}

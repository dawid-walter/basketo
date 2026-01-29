package com.dwalter.basketo.modules.identity.domain.model;

import com.dwalter.basketo.modules.identity.domain.events.PinGeneratedEvent;
import com.dwalter.basketo.modules.identity.domain.events.UserRegisteredEvent;
import com.dwalter.basketo.shared.domain.AggregateRoot;

import java.util.UUID;

public class User extends AggregateRoot {
    private final UUID id;
    private final Email email;
    private OneTimePin currentPin;

    private User(UUID id, Email email) {
        this.id = id;
        this.email = email;
    }

    public static User register(Email email) {
        User user = new User(UUID.randomUUID(), email);
        user.registerEvent(new UserRegisteredEvent(user.id, user.email));
        return user;
    }

    public void generateNewPin() {
        this.currentPin = OneTimePin.generate(15); // Valid for 15 minutes
        registerEvent(new PinGeneratedEvent(this.email, this.currentPin.code()));
    }

    public boolean verifyPin(String pinCode) {
        return currentPin != null && currentPin.isValid(pinCode);
    }

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public OneTimePin getCurrentPin() {
        return currentPin;
    }

    // Reconstruction from DB
    public static User restore(UUID id, Email email, OneTimePin pin) {
        User user = new User(id, email);
        user.currentPin = pin;
        return user;
    }
}

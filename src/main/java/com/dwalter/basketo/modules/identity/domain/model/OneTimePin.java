package com.dwalter.basketo.modules.identity.domain.model;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Random;

public record OneTimePin(String code, Instant expiresAt) {
    private static final Random RANDOM = new Random();

    public OneTimePin {
        Objects.requireNonNull(code);
        Objects.requireNonNull(expiresAt);
        if (code.length() != 6) {
            throw new IllegalArgumentException("PIN must be 6 digits");
        }
    }

    public static OneTimePin generate(int validityMinutes, Clock clock) {
        String code = String.format("%06d", RANDOM.nextInt(1000000));
        return new OneTimePin(code, Instant.now(clock).plusSeconds(validityMinutes * 60L));
    }

    public boolean isValid(String inputCode, Clock clock) {
        return this.code.equals(inputCode) && Instant.now(clock).isBefore(expiresAt);
    }
}

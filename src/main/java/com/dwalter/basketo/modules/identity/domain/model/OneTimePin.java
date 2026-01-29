package com.dwalter.basketo.modules.identity.domain.model;

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

    public static OneTimePin generate(int validityMinutes) {
        String code = String.format("%06d", RANDOM.nextInt(1000000));
        return new OneTimePin(code, Instant.now().plusSeconds(validityMinutes * 60L));
    }

    public boolean isValid(String inputCode) {
        return this.code.equals(inputCode) && Instant.now().isBefore(expiresAt);
    }
}

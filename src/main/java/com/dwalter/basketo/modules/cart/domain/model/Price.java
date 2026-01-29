package com.dwalter.basketo.modules.cart.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Price(BigDecimal value, String currency) {
    public Price {
        Objects.requireNonNull(value);
        Objects.requireNonNull(currency);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    public static Price of(double value, String currency) {
        return new Price(BigDecimal.valueOf(value), currency);
    }

    public Price multiply(int quantity) {
        return new Price(value.multiply(BigDecimal.valueOf(quantity)), currency);
    }
}

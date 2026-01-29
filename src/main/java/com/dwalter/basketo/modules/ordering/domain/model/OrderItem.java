package com.dwalter.basketo.modules.ordering.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(UUID productId, String productName, int quantity, BigDecimal unitPrice, String currency) {
    public BigDecimal totalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

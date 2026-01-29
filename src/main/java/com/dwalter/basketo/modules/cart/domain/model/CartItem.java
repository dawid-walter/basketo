package com.dwalter.basketo.modules.cart.domain.model;

import java.util.UUID;

public record CartItem(UUID productId, String productName, int quantity, Price unitPrice) {
    public CartItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    public Price totalPrice() {
        return unitPrice.multiply(quantity);
    }
}

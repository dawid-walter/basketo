package com.dwalter.basketo.modules.cart.infrastructure.adapters;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class CartItemJpaEntity {
    @Id
    private UUID id;

    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal priceAmount;
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private CartJpaEntity cart;
}

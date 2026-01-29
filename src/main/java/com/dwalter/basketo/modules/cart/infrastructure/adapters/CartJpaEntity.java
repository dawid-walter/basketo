package com.dwalter.basketo.modules.cart.infrastructure.adapters;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
class CartJpaEntity {
    @Id
    private UUID id;
    private String userEmail;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemJpaEntity> items = new ArrayList<>();

    public CartJpaEntity(UUID id, String userEmail) {
        this.id = id;
        this.userEmail = userEmail;
    }
}

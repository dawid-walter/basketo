package com.dwalter.basketo.modules.ordering.infrastructure.adapters;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class OrderItemJpaEntity {
    @Id
    @GeneratedValue
    private UUID id;
    
    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderJpaEntity order;
}

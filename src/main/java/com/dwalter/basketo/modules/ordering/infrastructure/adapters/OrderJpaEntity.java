package com.dwalter.basketo.modules.ordering.infrastructure.adapters;

import com.dwalter.basketo.modules.ordering.domain.model.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
class OrderJpaEntity {
    @Id
    private UUID id;
    private String userEmail;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    private Instant createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    public OrderJpaEntity(UUID id, String userEmail, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.userEmail = userEmail;
        this.status = status;
        this.createdAt = createdAt;
    }
}

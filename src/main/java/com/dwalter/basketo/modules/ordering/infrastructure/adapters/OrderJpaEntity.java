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
    @Column(nullable = false, unique = true)
    private String orderNumber;
    private String userEmail;

    // Shipping Address fields
    private String shippingFirstName;
    private String shippingLastName;
    private String shippingAddressLine;
    private String shippingCity;
    private String shippingPostalCode;
    private String shippingCountry;
    private String shippingPhone;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Instant createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    public OrderJpaEntity(UUID id, String orderNumber, String userEmail, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userEmail = userEmail;
        this.status = status;
        this.createdAt = createdAt;
    }
}

package com.dwalter.basketo.modules.ordering.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class OrderViewJpaEntity {
    @Id
    private UUID id;
    private String userEmail;
    private BigDecimal totalAmount;
    private String currency;
    private String status; // String representation of OrderStatus
    private Instant createdAt;
    private String itemsSummary; // Simplified list for list view
}

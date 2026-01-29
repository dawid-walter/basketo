package com.dwalter.basketo.modules.payment.infrastructure.adapters;

import com.dwalter.basketo.modules.payment.domain.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class PaymentJpaEntity {
    @Id
    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    private Instant createdAt;
}

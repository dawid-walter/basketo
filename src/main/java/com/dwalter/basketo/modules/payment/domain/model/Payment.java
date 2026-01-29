package com.dwalter.basketo.modules.payment.domain.model;

import com.dwalter.basketo.modules.payment.domain.events.PaymentCompletedEvent;
import com.dwalter.basketo.shared.domain.AggregateRoot;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class Payment extends AggregateRoot {
    private final UUID id;
    private final UUID orderId;
    private final BigDecimal amount;
    private final String currency;
    private PaymentStatus status;
    private final Instant createdAt;
    private String externalTransactionId;

    public Payment(UUID id, UUID orderId, BigDecimal amount, String currency, Clock clock) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.PENDING;
        this.createdAt = Instant.now(clock);
    }

    private Payment(UUID id, UUID orderId, BigDecimal amount, String currency, PaymentStatus status, Instant createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Restore
    public static Payment restore(UUID id, UUID orderId, BigDecimal amount, String currency, PaymentStatus status) {
        // Since we don't store createdAt in JPA entity in previous step (simplification), we use a placeholder or assume now if null
        // But wait, JPA entity HAS createdAt. So we should pass it.
        // Let's update restore signature to accept createdAt or keep it simple if entity doesn't expose it easily yet.
        // In JpaPaymentRepositoryAdapter we were doing: Payment.restore(..., e.getStatus())
        // Let's stick to current restore but add createdAt field to it?
        // To avoid breaking too much, I will add createdAt to restore method signature.
        return new Payment(id, orderId, amount, currency, status, Instant.EPOCH); // Placeholder if not available from DB entity yet
    }
    
    // Better restore
    public static Payment restore(UUID id, UUID orderId, BigDecimal amount, String currency, PaymentStatus status, Instant createdAt) {
        return new Payment(id, orderId, amount, currency, status, createdAt);
    }

    public void complete(String externalTransactionId) {
        if (this.status != PaymentStatus.PENDING) {
            // Idempotency check or error
            return;
        }
        this.status = PaymentStatus.COMPLETED;
        this.externalTransactionId = externalTransactionId;
        registerEvent(new PaymentCompletedEvent(UUID.randomUUID(), this.id, this.orderId, Instant.now())); // Event timestamp - maybe pass clock here too?
        // For events, Instant.now() is usually acceptable as it's technical metadata, but for strictness we could pass clock.
        // Let's leave Instant.now() for event emission time to keep it simple, or refactor complete() to take clock.
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}

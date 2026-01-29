package com.dwalter.basketo.modules.payment.domain.model;

import com.dwalter.basketo.modules.payment.domain.events.PaymentCompletedEvent;
import com.dwalter.basketo.shared.domain.AggregateRoot;
import java.math.BigDecimal;
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

    public Payment(UUID id, UUID orderId, BigDecimal amount, String currency) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.PENDING;
        this.createdAt = Instant.now();
    }

    // Restore
    public static Payment restore(UUID id, UUID orderId, BigDecimal amount, String currency, PaymentStatus status) {
        Payment p = new Payment(id, orderId, amount, currency);
        p.status = status;
        return p;
    }

    public void complete(String externalTransactionId) {
        if (this.status != PaymentStatus.PENDING) {
            // Idempotency check or error
            return;
        }
        this.status = PaymentStatus.COMPLETED;
        this.externalTransactionId = externalTransactionId;
        registerEvent(new PaymentCompletedEvent(this.id, this.orderId));
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentStatus getStatus() { return status; }
}

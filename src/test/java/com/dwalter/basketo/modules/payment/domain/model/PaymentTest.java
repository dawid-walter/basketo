package com.dwalter.basketo.modules.payment.domain.model;

import com.dwalter.basketo.modules.payment.domain.events.PaymentCompletedEvent;
import com.dwalter.basketo.shared.domain.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    private final Clock clock = Clock.systemUTC();

    @Test
    void shouldCompletePayment() {
        // given
        Payment payment = new Payment(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, "PLN", clock);
        String externalId = "txn_12345";

        // when
        payment.complete(externalId);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        
        List<DomainEvent> events = payment.getDomainEvents();
        assertThat(events)
                .hasSize(1)
                .first()
                .isInstanceOf(PaymentCompletedEvent.class)
                .extracting(e -> ((PaymentCompletedEvent) e).paymentId())
                .isEqualTo(payment.getId());
    }

    @Test
    void shouldBeIdempotentWhenCompletingTwice() {
        // given
        Payment payment = new Payment(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, "PLN", clock);
        payment.complete("txn_1");
        
        // when
        payment.complete("txn_2"); // Should be ignored

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        // Should have only 1 event from the first completion
        assertThat(payment.getDomainEvents()).hasSize(1);
    }
}

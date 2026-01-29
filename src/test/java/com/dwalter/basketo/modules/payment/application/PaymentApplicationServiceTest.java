package com.dwalter.basketo.modules.payment.application;

import com.dwalter.basketo.modules.payment.domain.events.PaymentCompletedEvent;
import com.dwalter.basketo.modules.payment.domain.model.Payment;
import com.dwalter.basketo.modules.payment.domain.model.PaymentStatus;
import com.dwalter.basketo.modules.payment.domain.ports.PaymentGateway;
import com.dwalter.basketo.modules.payment.domain.ports.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentApplicationServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Clock clock;
    private PaymentApplicationService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneId.of("UTC"));
        service = new PaymentApplicationService(paymentRepository, paymentGateway, eventPublisher, clock);
    }

    @Test
    void shouldInitPayment() {
        // given
        UUID orderId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        String currency = "PLN";
        String paymentLink = "http://pay.me/123";

        when(paymentGateway.initiatePayment(any(Payment.class))).thenReturn(paymentLink);

        // when
        service.initPayment(orderId, amount, currency);

        // then
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        
        Payment savedPayment = paymentCaptor.getValue();
        assertThat(savedPayment.getOrderId()).isEqualTo(orderId);
        assertThat(savedPayment.getAmount()).isEqualTo(amount);
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(savedPayment.getCreatedAt()).isEqualTo(Instant.now(clock));
    }

    @Test
    void shouldHandlePaymentWebhook() {
        // given
        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment(paymentId, UUID.randomUUID(), BigDecimal.TEN, "PLN", clock);
        
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // when
        service.handlePaymentWebhook(paymentId, "tx_123");

        // then
        // Verify payment updated
        verify(paymentRepository).save(payment);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);

        // Verify Event
        ArgumentCaptor<PaymentCompletedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().paymentId()).isEqualTo(paymentId);
    }

    @Test
    void shouldFailWebhookIfPaymentNotFound() {
        // given
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.handlePaymentWebhook(paymentId, "tx_123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment not found");
    }
}
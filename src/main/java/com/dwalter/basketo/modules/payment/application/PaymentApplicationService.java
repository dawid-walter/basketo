package com.dwalter.basketo.modules.payment.application;

import com.dwalter.basketo.modules.payment.domain.model.Payment;
import com.dwalter.basketo.modules.payment.domain.ports.PaymentGateway;
import com.dwalter.basketo.modules.payment.domain.ports.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentApplicationService {
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void initPayment(UUID orderId, BigDecimal amount, String currency) {
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            return; // Payment already initiated for this order
        }

        Payment payment = new Payment(UUID.randomUUID(), orderId, amount, currency);
        paymentRepository.save(payment);
        
        String link = paymentGateway.initiatePayment(payment);
        // In a real app, we might email this link or return it, 
        // but here we just assume the user gets redirected.
        // For our headless flow, we rely on the plugin handling the redirect if we returned it,
        // but since this is triggered by Event, we'll just log it in the Fake Adapter.
    }

    @Transactional
    public void handlePaymentWebhook(UUID paymentId, String externalTransactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        
        payment.complete(externalTransactionId);
        paymentRepository.save(payment);

        payment.getDomainEvents().forEach(eventPublisher::publishEvent);
        payment.clearDomainEvents();
    }
}

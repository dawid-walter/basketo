package com.dwalter.basketo.modules.payment.infrastructure.adapters;

import com.dwalter.basketo.modules.payment.application.PaymentApplicationService;
import com.dwalter.basketo.modules.payment.domain.model.Payment;
import com.dwalter.basketo.modules.payment.domain.ports.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
class PaymentWebhookController {
    private final PaymentApplicationService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/webhook/{orderId}")
    public ResponseEntity<Void> simulateWebhook(@PathVariable UUID orderId) {
        // Find payment by orderId instead of paymentId
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for order: " + orderId));

        paymentService.handlePaymentWebhook(payment.getId(), "TX-" + UUID.randomUUID());
        return ResponseEntity.ok().build();
    }
}

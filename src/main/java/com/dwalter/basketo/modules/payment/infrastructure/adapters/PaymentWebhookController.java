package com.dwalter.basketo.modules.payment.infrastructure.adapters;

import com.dwalter.basketo.modules.payment.application.PaymentApplicationService;
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

    @PostMapping("/webhook/{paymentId}")
    public ResponseEntity<Void> simulateWebhook(@PathVariable UUID paymentId) {
        paymentService.handlePaymentWebhook(paymentId, "TX-" + UUID.randomUUID());
        return ResponseEntity.ok().build();
    }
}

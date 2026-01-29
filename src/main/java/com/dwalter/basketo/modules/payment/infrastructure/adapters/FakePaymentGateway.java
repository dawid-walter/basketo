package com.dwalter.basketo.modules.payment.infrastructure.adapters;

import com.dwalter.basketo.modules.payment.domain.model.Payment;
import com.dwalter.basketo.modules.payment.domain.ports.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class FakePaymentGateway implements PaymentGateway {
    @Override
    public String initiatePayment(Payment payment) {
        String link = "https://sandbox.payments.com/pay/" + payment.getId();
        log.info("==========================================");
        log.info("PAYMENT INITIATED: " + link);
        log.info("Use POST /api/payments/webhook/" + payment.getId() + " to simulate success");
        log.info("==========================================");
        return link;
    }
}

package com.dwalter.basketo.modules.payment.domain.ports;

import com.dwalter.basketo.modules.payment.domain.model.Payment;

public interface PaymentGateway {
    // Returns a payment link (or mock URL)
    String initiatePayment(Payment payment);
}

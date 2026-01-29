package com.dwalter.basketo.modules.payment.infrastructure.adapters;

import com.dwalter.basketo.modules.ordering.domain.events.OrderCreatedEvent;
import com.dwalter.basketo.modules.payment.application.PaymentApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PaymentOrderCreatedListener {
    private final PaymentApplicationService paymentService;

    @EventListener
    @Async
    public void handle(OrderCreatedEvent event) {
        paymentService.initPayment(event.orderId(), event.totalAmount(), event.currency());
    }
}

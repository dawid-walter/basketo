package com.dwalter.basketo.modules.ordering.infrastructure.adapters;

import com.dwalter.basketo.modules.ordering.application.OrderApplicationService;
import com.dwalter.basketo.modules.ordering.domain.model.Order;
import com.dwalter.basketo.modules.ordering.domain.ports.OrderRepository;
import com.dwalter.basketo.modules.payment.domain.events.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class OrderingPaymentCompletedListener {
    
    private final OrderRepository orderRepository;

    @EventListener
    @Async
    @Transactional
    public void handle(PaymentCompletedEvent event) {
        orderRepository.findById(event.orderId()).ifPresent(order -> {
            order.markAsPaid();
            orderRepository.save(order);
        });
    }
}

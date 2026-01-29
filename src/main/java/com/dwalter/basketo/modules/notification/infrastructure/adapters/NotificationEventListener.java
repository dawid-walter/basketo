package com.dwalter.basketo.modules.notification.infrastructure.adapters;

import com.dwalter.basketo.modules.identity.domain.events.PinGeneratedEvent;
import com.dwalter.basketo.modules.notification.domain.ports.MailSender;
import com.dwalter.basketo.modules.ordering.domain.events.OrderCreatedEvent;
import com.dwalter.basketo.modules.payment.domain.events.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class NotificationEventListener {

    private final MailSender mailSender;

    @Async
    @EventListener
    public void handle(PinGeneratedEvent event) {
        mailSender.send(
                event.email().value(),
                "Your Login PIN",
                "Your temporary PIN is: " + event.pin()
        );
    }

    @Async
    @EventListener
    public void handle(OrderCreatedEvent event) {
        mailSender.send(
                event.userEmail(),
                "Order Confirmation",
                "Thank you for your order #" + event.orderId() + ". Total: " + event.totalAmount() + " " + event.currency()
        );
    }

    @Async
    @EventListener
    public void handle(PaymentCompletedEvent event) {
        // In a real app, we would need the email from the order
        // For simplicity, we just log it or we would fetch order details
        mailSender.send(
                "user-from-order-" + event.orderId(), 
                "Payment Received",
                "Your payment for order #" + event.orderId() + " has been confirmed."
        );
    }
}

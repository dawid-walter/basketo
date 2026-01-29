package com.dwalter.basketo.modules.notification.infrastructure.adapters;

import com.dwalter.basketo.modules.identity.domain.events.PinGeneratedEvent;
import com.dwalter.basketo.modules.notification.domain.ports.MailSender;
import com.dwalter.basketo.modules.ordering.domain.events.OrderCreatedEvent;
import com.dwalter.basketo.modules.payment.domain.events.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class NotificationEventListener {

    private final MailSender mailSender;

    @Async
    @EventListener
    public void handle(PinGeneratedEvent event) {
        mailSender.sendHtml(
                event.email().value(),
                "Your Login PIN",
                "pin-email",
                Map.of("pin", event.pin())
        );
    }

    @Async
    @EventListener
    public void handle(OrderCreatedEvent event) {
        mailSender.sendHtml(
                event.userEmail(),
                "Order Confirmation",
                "order-confirmation",
                Map.of(
                        "email", event.userEmail(),
                        "orderId", event.orderId(),
                        "items", event.items(),
                        "totalAmount", event.totalAmount(),
                        "currency", event.currency()
                )
        );
    }
// ...


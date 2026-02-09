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
        var templateVars = new java.util.HashMap<String, Object>();
        templateVars.put("email", event.userEmail());
        templateVars.put("orderId", event.orderId());
        templateVars.put("orderNumber", event.orderNumber());
        templateVars.put("items", event.items());
        templateVars.put("totalAmount", event.totalAmount());
        templateVars.put("currency", event.currency());

        if (event.shippingAddress() != null) {
            var shipping = event.shippingAddress();
            templateVars.put("shippingAddress", Map.of(
                    "firstName", shipping.firstName(),
                    "lastName", shipping.lastName(),
                    "addressLine", shipping.addressLine(),
                    "city", shipping.city(),
                    "postalCode", shipping.postalCode(),
                    "country", shipping.country(),
                    "phone", shipping.phone()
            ));
        }

        mailSender.sendHtml(
                event.userEmail(),
                "Order Confirmation",
                "order-confirmation",
                templateVars
        );
    }

    @Async
    @EventListener
    public void handle(PaymentCompletedEvent event) {
        mailSender.send(
                "user-from-order-" + event.orderId(), 
                "Payment Received",
                "Your payment for order #" + event.orderId() + " has been confirmed."
        );
    }
}
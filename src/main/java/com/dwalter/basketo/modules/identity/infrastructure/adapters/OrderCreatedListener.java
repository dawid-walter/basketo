package com.dwalter.basketo.modules.identity.infrastructure.adapters;

import com.dwalter.basketo.modules.identity.application.IdentityApplicationService;
import com.dwalter.basketo.modules.ordering.domain.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class OrderCreatedListener {

    private final IdentityApplicationService identityService;

    /**
     * Handle order created event synchronously to avoid race conditions
     * when multiple orders are created with the same email.
     *
     * This ensures the user record exists in the database (find-or-create)
     * without generating a PIN or sending authentication emails.
     *
     * PIN is only generated and sent when the customer explicitly requests
     * it through the order tracking interface (via /api/auth/request-pin-by-order).
     */
    @EventListener
    @Transactional
    public void handle(OrderCreatedEvent event) {
        // Ensure user exists without generating PIN
        // Order confirmation email is sent by NotificationEventListener
        identityService.ensureUserExists(event.userEmail());
    }
}

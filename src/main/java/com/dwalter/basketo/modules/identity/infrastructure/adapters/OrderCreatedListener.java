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
     * The find-or-create logic in identityService.requestLoginPin ensures
     * that we don't try to create duplicate users.
     */
    @EventListener
    @Transactional
    public void handle(OrderCreatedEvent event) {
        // Automatically create account/send PIN when order is created
        identityService.requestLoginPin(event.userEmail());
    }
}

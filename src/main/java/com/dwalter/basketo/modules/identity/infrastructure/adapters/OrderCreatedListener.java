package com.dwalter.basketo.modules.identity.infrastructure.adapters;

import com.dwalter.basketo.modules.identity.application.IdentityApplicationService;
import com.dwalter.basketo.modules.ordering.domain.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class OrderCreatedListener {

    private final IdentityApplicationService identityService;

    @EventListener
    @Async
    public void handle(OrderCreatedEvent event) {
        // Automatically create account/send PIN when order is created
        identityService.requestLoginPin(event.userEmail());
    }
}

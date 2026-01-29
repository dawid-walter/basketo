package com.dwalter.basketo.modules.ordering.query;

import com.dwalter.basketo.modules.ordering.domain.events.OrderCreatedEvent;
import com.dwalter.basketo.modules.ordering.domain.events.OrderPaidEvent;
import com.dwalter.basketo.modules.ordering.domain.model.OrderItem;
import com.dwalter.basketo.modules.ordering.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class OrderViewEventListener {

    private final OrderViewRepository repository;

    @Async
    @EventListener
    @Transactional
    public void handle(OrderCreatedEvent event) {
        String itemsSummary = event.items().stream()
                .map(item -> item.productName() + " x" + item.quantity())
                .collect(Collectors.joining(", "));

        OrderViewJpaEntity view = new OrderViewJpaEntity(
                event.orderId(),
                event.userEmail(),
                event.totalAmount(),
                event.currency(),
                OrderStatus.CREATED.name(),
                event.occurredAt(),
                itemsSummary
        );
        repository.save(view);
    }

    @Async
    @EventListener
    @Transactional
    public void handle(OrderPaidEvent event) {
        repository.findById(event.orderId()).ifPresent(view -> {
            view.setStatus(OrderStatus.PAID.name());
            repository.save(view);
        });
    }
}

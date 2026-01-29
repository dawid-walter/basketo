package com.dwalter.basketo.modules.ordering.application;

import com.dwalter.basketo.modules.ordering.domain.model.Order;
import com.dwalter.basketo.modules.ordering.domain.model.OrderItem;
import com.dwalter.basketo.modules.ordering.domain.ports.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UUID createOrder(CreateOrderCommand command) {
        List<OrderItem> items = command.items().stream()
                .map(item -> new OrderItem(
                        item.productId(),
                        item.productName(),
                        item.quantity(),
                        item.unitPrice(),
                        item.currency()
                ))
                .collect(java.util.stream.Collectors.toList());

        Order order = Order.create(command.userEmail(), items);
        orderRepository.save(order);

        // Publish domain events to Spring Application Context
        order.getDomainEvents().forEach(eventPublisher::publishEvent);
        order.clearDomainEvents();

        return order.getId();
    }

    public List<Order> getUserOrders(String email) {
        return orderRepository.findByUserEmail(email);
    }

    public record CreateOrderCommand(String userEmail, List<OrderItemCommand> items) {}
    public record OrderItemCommand(UUID productId, String productName, int quantity, BigDecimal unitPrice, String currency) {}
}

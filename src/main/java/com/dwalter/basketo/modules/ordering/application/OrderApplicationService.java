package com.dwalter.basketo.modules.ordering.application;

import com.dwalter.basketo.modules.ordering.domain.model.Order;
import com.dwalter.basketo.modules.ordering.domain.model.OrderItem;
import com.dwalter.basketo.modules.ordering.domain.model.ShippingAddress;
import com.dwalter.basketo.modules.ordering.domain.ports.OrderNumberGenerator;
import com.dwalter.basketo.modules.ordering.domain.ports.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {
    private final OrderRepository orderRepository;
    private final OrderNumberGenerator orderNumberGenerator;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional
    public CreateOrderResult createOrder(CreateOrderCommand command) {
        List<OrderItem> items = command.items().stream()
                .map(item -> new OrderItem(
                        item.productId(),
                        item.productName(),
                        item.quantity(),
                        item.unitPrice(),
                        item.currency()
                ))
                .collect(java.util.stream.Collectors.toList());

        String orderNumber = orderNumberGenerator.generateOrderNumber();
        Order order = Order.create(orderNumber, command.userEmail(), command.shippingAddress(), items, clock);
        orderRepository.save(order);

        // Publish domain events to Spring Application Context
        order.getDomainEvents().forEach(eventPublisher::publishEvent);
        order.clearDomainEvents();

        return new CreateOrderResult(order.getId(), order.getOrderNumber());
    }

    public List<Order> getUserOrders(String email) {
        return orderRepository.findByUserEmail(email);
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNumber));
    }

    public record CreateOrderCommand(String userEmail, ShippingAddress shippingAddress, List<OrderItemCommand> items) {}
    public record OrderItemCommand(UUID productId, String productName, int quantity, BigDecimal unitPrice, String currency) {}
    public record CreateOrderResult(UUID orderId, String orderNumber) {}
}

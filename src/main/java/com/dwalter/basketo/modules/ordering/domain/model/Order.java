package com.dwalter.basketo.modules.ordering.domain.model;

import com.dwalter.basketo.modules.ordering.domain.events.OrderCreatedEvent;
import com.dwalter.basketo.modules.ordering.domain.events.OrderPaidEvent;
import com.dwalter.basketo.shared.domain.AggregateRoot;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot {
    private final UUID id;
    private final String orderNumber;
    private final String userEmail;
    private final ShippingAddress shippingAddress;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final Instant createdAt;

    // Constructor for new orders
    public static Order create(String orderNumber, String userEmail, ShippingAddress shippingAddress, List<OrderItem> items, Clock clock) {
        Order order = new Order(UUID.randomUUID(), orderNumber, userEmail, shippingAddress, items, OrderStatus.CREATED, Instant.now(clock));
        order.registerEvent(new OrderCreatedEvent(
                UUID.randomUUID(),
                order.id,
                order.orderNumber,
                order.userEmail,
                order.shippingAddress,
                order.totalAmount(),
                order.currency(),
                order.items,
                order.createdAt
        ));
        return order;
    }

    // Constructor for reconstruction
    public static Order restore(UUID id, String orderNumber, String userEmail, ShippingAddress shippingAddress, List<OrderItem> items, OrderStatus status, Instant createdAt) {
        return new Order(id, orderNumber, userEmail, shippingAddress, items, status, createdAt);
    }

    private Order(UUID id, String orderNumber, String userEmail, ShippingAddress shippingAddress, List<OrderItem> items, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userEmail = userEmail;
        this.shippingAddress = shippingAddress;
        this.items = items;
        this.status = status;
        this.createdAt = createdAt;
    }
// ...

    public BigDecimal totalAmount() {
        return items.stream()
                .map(OrderItem::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String currency() {
        return items.isEmpty() ? "PLN" : items.getFirst().currency();
    }

    public void markAsPaid() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay for cancelled order");
        }
        this.status = OrderStatus.PAID;
        registerEvent(new OrderPaidEvent(this.id));
    }

    public UUID getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getUserEmail() { return userEmail; }
    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}

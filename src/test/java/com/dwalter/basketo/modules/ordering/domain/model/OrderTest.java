package com.dwalter.basketo.modules.ordering.domain.model;

import com.dwalter.basketo.modules.ordering.domain.events.OrderCreatedEvent;
import com.dwalter.basketo.shared.domain.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void shouldCalculateTotalAmount() {
        // given
        OrderItem item1 = new OrderItem(UUID.randomUUID(), "Product 1", 2, new BigDecimal("100.00"), "PLN");
        OrderItem item2 = new OrderItem(UUID.randomUUID(), "Product 2", 1, new BigDecimal("50.00"), "PLN");
        
        // when
        Order order = Order.create("test@example.com", List.of(item1, item2));

        // then
        // (2 * 100) + (1 * 50) = 250
        assertThat(order.totalAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(order.currency()).isEqualTo("PLN");
    }

    @Test
    void shouldCreateOrderWithStatusCreated() {
        // when
        Order order = Order.create("test@example.com", List.of());

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldEmitOrderCreatedEvent() {
        // when
        Order order = Order.create("test@example.com", List.of());

        // then
        List<DomainEvent> events = order.getDomainEvents();
        assertThat(events)
                .hasSize(1)
                .first()
                .isInstanceOf(OrderCreatedEvent.class);
    }

    @Test
    void shouldMarkOrderAsPaid() {
        // given
        Order order = Order.create("test@example.com", List.of());

        // when
        order.markAsPaid();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldNotAllowPaymentForCancelledOrder() {
        // given
        // We need to simulate a cancelled order. Since we don't have cancel() method exposed yet,
        // we can use the restore() method to create an order in CANCELLED state.
        Order order = Order.restore(UUID.randomUUID(), "email", List.of(), OrderStatus.CANCELLED, java.time.Instant.now());

        // when / then
        assertThatThrownBy(order::markAsPaid)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot pay for cancelled order");
    }
}

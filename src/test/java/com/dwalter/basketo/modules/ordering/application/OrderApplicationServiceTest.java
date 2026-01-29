package com.dwalter.basketo.modules.ordering.application;

import com.dwalter.basketo.modules.ordering.application.OrderApplicationService.CreateOrderCommand;
import com.dwalter.basketo.modules.ordering.application.OrderApplicationService.OrderItemCommand;
import com.dwalter.basketo.modules.ordering.domain.events.OrderCreatedEvent;
import com.dwalter.basketo.modules.ordering.domain.model.Order;
import com.dwalter.basketo.modules.ordering.domain.ports.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Clock clock;

    @InjectMocks
    private OrderApplicationService service;

    @Test
    void shouldCreateOrder() {
        // given
        when(clock.instant()).thenReturn(Instant.parse("2026-01-01T10:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        String email = "test@example.com";
        OrderItemCommand itemCmd = new OrderItemCommand(
                UUID.randomUUID(), "Product", 2, new BigDecimal("100.00"), "PLN"
        );
        CreateOrderCommand command = new CreateOrderCommand(email, List.of(itemCmd));

        // when
        UUID orderId = service.createOrder(command);

        // then
        assertThat(orderId).isNotNull();

        // Verify Save
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        
        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getId()).isEqualTo(orderId);
        assertThat(savedOrder.getUserEmail()).isEqualTo(email);

        // Verify Event
        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().orderId()).isEqualTo(orderId);
    }

    @Test
    void shouldGetUserOrders() {
        // given
        String email = "test@example.com";
        Order order = Order.restore(UUID.randomUUID(), email, List.of(), com.dwalter.basketo.modules.ordering.domain.model.OrderStatus.CREATED, Instant.now());
        when(orderRepository.findByUserEmail(email)).thenReturn(List.of(order));

        // when
        List<Order> orders = service.getUserOrders(email);

        // then
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0)).isEqualTo(order);
    }
}
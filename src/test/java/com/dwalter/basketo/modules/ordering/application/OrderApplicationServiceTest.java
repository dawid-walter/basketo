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
import static org.mockito.ArgumentMatchers.any;
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
// ...
    @Test
    void shouldGetUserOrders() {
        // given
        // No clock interaction needed here
        
        String email = "test@example.com";
        Order order = Order.create(email, List.of(), Clock.systemUTC()); // Use real clock for test object creation
        when(orderRepository.findByUserEmail(email)).thenReturn(List.of(order));

        // when
        List<Order> orders = service.getUserOrders(email);

        // then
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0)).isEqualTo(order);
    }
}

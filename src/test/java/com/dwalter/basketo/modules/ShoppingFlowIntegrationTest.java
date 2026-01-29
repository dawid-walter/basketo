package com.dwalter.basketo.modules;

import com.dwalter.basketo.AbstractIntegrationTest;
import com.dwalter.basketo.modules.cart.application.CartApplicationService;
import com.dwalter.basketo.modules.cart.application.CartApplicationService.CartItemCommand;
import com.dwalter.basketo.modules.ordering.domain.model.Order;
import com.dwalter.basketo.modules.ordering.domain.ports.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CartApplicationService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Transactional
    void shouldCreateOrderFromCart() {
        // given
        String email = "integration@test.com";
        CartItemCommand item = new CartItemCommand(
                UUID.randomUUID(), "Integration Item", 2, new BigDecimal("100.00"), "PLN"
        );

        // when
        UUID cartId = cartService.initializeCart(List.of(item), email);
        UUID orderId = cartService.checkoutCart(cartId);

        // then
        // Check if order exists in DB (integration with Persistence)
        List<Order> orders = orderRepository.findByUserEmail(email);
        assertThat(orders).hasSize(1);
        
        Order order = orders.get(0);
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.totalAmount()).isEqualByComparingTo(new BigDecimal("200.00")); // 2 * 100
    }
}

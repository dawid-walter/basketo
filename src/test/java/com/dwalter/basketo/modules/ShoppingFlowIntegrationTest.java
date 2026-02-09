package com.dwalter.basketo.modules;

import com.dwalter.basketo.AbstractIntegrationTest;
import com.dwalter.basketo.modules.cart.application.CartApplicationService;
import com.dwalter.basketo.modules.cart.application.CartApplicationService.CartItemCommand;
import com.dwalter.basketo.modules.identity.domain.ports.UserRepository;
import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.modules.ordering.domain.model.Order;
import com.dwalter.basketo.modules.ordering.domain.model.ShippingAddress;
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

    @Autowired
    private UserRepository userRepository;

    private ShippingAddress createTestShippingAddress() {
        return new ShippingAddress(
                "John",
                "Doe",
                "123 Main St",
                "Warsaw",
                "00-001",
                "Poland",
                "+48123456789"
        );
    }

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
        var result = cartService.checkoutCart(cartId, createTestShippingAddress());

        // then
        // Check if order exists in DB (integration with Persistence)
        List<Order> orders = orderRepository.findByUserEmail(email);
        assertThat(orders).hasSize(1);

        Order order = orders.getFirst();
        assertThat(order.getId()).isEqualTo(result.orderId());
        assertThat(order.totalAmount()).isEqualByComparingTo(new BigDecimal("200.00")); // 2 * 100
    }

    @Test
    @Transactional
    void shouldAllowMultipleOrdersFromSameEmail() {
        // given - same email for multiple orders
        String email = "repeat.customer@test.com";

        CartItemCommand item1 = new CartItemCommand(
                UUID.randomUUID(), "First Item", 1, new BigDecimal("50.00"), "PLN"
        );
        CartItemCommand item2 = new CartItemCommand(
                UUID.randomUUID(), "Second Item", 2, new BigDecimal("75.00"), "PLN"
        );

        // when - create first order
        UUID cartId1 = cartService.initializeCart(List.of(item1), email);
        var result1 = cartService.checkoutCart(cartId1, createTestShippingAddress());

        // and - create second order with same email
        UUID cartId2 = cartService.initializeCart(List.of(item2), email);
        var result2 = cartService.checkoutCart(cartId2, createTestShippingAddress());

        // then - both orders should be created successfully
        List<Order> orders = orderRepository.findByUserEmail(email);
        assertThat(orders).hasSize(2);
        assertThat(orders)
                .extracting(Order::getId)
                .containsExactlyInAnyOrder(result1.orderId(), result2.orderId());

        // and - user should be created only once
        assertThat(userRepository.findByEmail(new Email(email))).isPresent();
    }
}

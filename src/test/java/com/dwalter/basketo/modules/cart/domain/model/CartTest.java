package com.dwalter.basketo.modules.cart.domain.model;

import com.dwalter.basketo.modules.cart.domain.events.CartCheckedOutEvent;
import com.dwalter.basketo.shared.domain.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class
CartTest {

    @Test
    void shouldAddItemsToCart() {
        // given
        Cart cart = new Cart(UUID.randomUUID());
        CartItem item = new CartItem(
                UUID.randomUUID(),
                "Laptop",
                1,
                new Price(new BigDecimal("5000.00"), "PLN")
        );

        // when
        cart.addItems(List.of(item));

        // then
        assertThat(cart.getItems())
                .hasSize(1)
                .containsExactly(item);
    }

    @Test
    void shouldEmitEventOnCheckout() {
        // given
        Cart cart = new Cart(UUID.randomUUID());
        UUID orderId = UUID.randomUUID();

        // when
        cart.checkout(orderId);

        // then
        List<DomainEvent> events = cart.getDomainEvents();
        assertThat(events)
                .hasSize(1)
                .first()
                .isInstanceOf(CartCheckedOutEvent.class)
                .extracting(e -> ((CartCheckedOutEvent) e).orderId())
                .isEqualTo(orderId);
    }

    @Test
    void shouldNotAllowNegativePrice() {
        // when / then
        assertThatThrownBy(() -> new Price(new BigDecimal("-10.00"), "PLN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price cannot be negative");
    }

    @Test
    void shouldNotAllowZeroOrNegativeQuantity() {
        // when / then
        assertThatThrownBy(() -> new CartItem(
                UUID.randomUUID(), "Test", 0, new Price(BigDecimal.TEN, "PLN")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");
    }
}

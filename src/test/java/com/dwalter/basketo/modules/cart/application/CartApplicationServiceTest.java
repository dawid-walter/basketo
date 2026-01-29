package com.dwalter.basketo.modules.cart.application;

import com.dwalter.basketo.modules.cart.application.CartApplicationService.CartItemCommand;
import com.dwalter.basketo.modules.cart.domain.events.CartCheckedOutEvent;
import com.dwalter.basketo.modules.cart.domain.model.Cart;
import com.dwalter.basketo.modules.cart.domain.ports.CartRepository;
import com.dwalter.basketo.modules.cart.domain.ports.OrderingGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartApplicationServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderingGateway orderingGateway;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CartApplicationService service;

    @Test
    void shouldInitializeCart() {
        // given
        String email = "test@example.com";
        CartItemCommand itemCmd = new CartItemCommand(
                UUID.randomUUID(), "Product", 1, BigDecimal.TEN, "PLN"
        );

        // when
        UUID cartId = service.initializeCart(List.of(itemCmd), email);

        // then
        assertThat(cartId).isNotNull();
        
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(cartCaptor.capture());
        
        Cart savedCart = cartCaptor.getValue();
        assertThat(savedCart.getId()).isEqualTo(cartId);
        assertThat(savedCart.getUserEmail()).isEqualTo(email);
        assertThat(savedCart.getItems()).hasSize(1);
    }

    @Test
    void shouldCheckoutCart() {
        // given
        UUID cartId = UUID.randomUUID();
        String email = "test@example.com";
        UUID orderId = UUID.randomUUID();

        Cart cart = new Cart(cartId);
        cart.assignUser(email);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(orderingGateway.createOrder(eq(email), anyList())).thenReturn(orderId);

        // when
        UUID resultOrderId = service.checkoutCart(cartId);

        // then
        assertThat(resultOrderId).isEqualTo(orderId);

        // Verify Gateway called
        verify(orderingGateway).createOrder(eq(email), anyList());

        // Verify Event published
        ArgumentCaptor<CartCheckedOutEvent> eventCaptor = ArgumentCaptor.forClass(CartCheckedOutEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        assertThat(eventCaptor.getValue().orderId()).isEqualTo(orderId);
        assertThat(eventCaptor.getValue().cartId()).isEqualTo(cartId);
    }

    @Test
    void shouldFailCheckoutIfCartNotFound() {
        // given
        UUID cartId = UUID.randomUUID();
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.checkoutCart(cartId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cart not found");
    }

    @Test
    void shouldFailCheckoutIfUserNotAssigned() {
        // given
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId); // No user assigned
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        // when / then
        assertThatThrownBy(() -> service.checkoutCart(cartId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cart has no user assigned");
    }
}

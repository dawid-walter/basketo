package com.dwalter.basketo.modules.cart.application;

import com.dwalter.basketo.modules.cart.domain.model.Cart;
import com.dwalter.basketo.modules.cart.domain.model.CartItem;
import com.dwalter.basketo.modules.cart.domain.model.Price;
import com.dwalter.basketo.modules.cart.domain.ports.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartApplicationService {

    private final CartRepository cartRepository;
    private final com.dwalter.basketo.modules.cart.domain.ports.OrderingGateway orderingGateway;

    @Transactional
    public UUID initializeCart(List<CartItemCommand> items, String userEmail) {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId);

        List<CartItem> domainItems = items.stream()
                .map(this::toDomainItem)
                .collect(Collectors.toList());

        cart.addItems(domainItems);
        
        if (userEmail != null && !userEmail.isBlank()) {
            cart.assignUser(userEmail);
        }

        cartRepository.save(cart);
        return cartId;
    }

    @Transactional
    public UUID checkoutCart(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found: " + cartId));
        
        if (cart.getUserEmail() == null) {
            throw new IllegalStateException("Cart has no user assigned");
        }

        return orderingGateway.createOrder(cart.getUserEmail(), cart.getItems());
    }

    private CartItem toDomainItem(CartItemCommand cmd) {
        return new CartItem(
                cmd.productId(),
                cmd.productName(),
                cmd.quantity(),
                new Price(cmd.price(), cmd.currency())
        );
    }

    public record CartItemCommand(
            UUID productId,
            String productName,
            int quantity,
            BigDecimal price,
            String currency
    ) {}
}

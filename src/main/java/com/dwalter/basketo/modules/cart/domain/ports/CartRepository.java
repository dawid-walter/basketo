package com.dwalter.basketo.modules.cart.domain.ports;

import com.dwalter.basketo.modules.cart.domain.model.Cart;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository {
    void save(Cart cart);
    Optional<Cart> findById(UUID id);
}

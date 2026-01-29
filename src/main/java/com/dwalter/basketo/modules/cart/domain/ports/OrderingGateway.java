package com.dwalter.basketo.modules.cart.domain.ports;

import com.dwalter.basketo.modules.cart.domain.model.CartItem;
import java.util.List;
import java.util.UUID;

public interface OrderingGateway {
    UUID createOrder(String userEmail, List<CartItem> items);
}

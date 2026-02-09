package com.dwalter.basketo.modules.cart.domain.ports;

import com.dwalter.basketo.modules.cart.domain.model.CartItem;
import com.dwalter.basketo.modules.ordering.domain.model.ShippingAddress;
import java.util.List;

public interface OrderingGateway {
    OrderingResult createOrder(String userEmail, ShippingAddress shippingAddress, List<CartItem> items);

    record OrderingResult(java.util.UUID orderId, String orderNumber) {}
}

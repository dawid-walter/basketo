package com.dwalter.basketo.modules.cart.infrastructure.adapters;

import com.dwalter.basketo.modules.cart.domain.model.CartItem;
import com.dwalter.basketo.modules.cart.domain.ports.OrderingGateway;
import com.dwalter.basketo.modules.ordering.application.OrderApplicationService;
import com.dwalter.basketo.modules.ordering.application.OrderApplicationService.CreateOrderCommand;
import com.dwalter.basketo.modules.ordering.application.OrderApplicationService.CreateOrderResult;
import com.dwalter.basketo.modules.ordering.application.OrderApplicationService.OrderItemCommand;
import com.dwalter.basketo.modules.ordering.domain.model.ShippingAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class DirectOrderingGateway implements OrderingGateway {

    private final OrderApplicationService orderService;

    @Override
    public OrderingResult createOrder(String userEmail, ShippingAddress shippingAddress, List<CartItem> items) {
        List<OrderItemCommand> itemCommands = items.stream()
                .map(item -> new OrderItemCommand(
                        item.productId(),
                        item.productName(),
                        item.quantity(),
                        item.unitPrice().value(),
                        item.unitPrice().currency()
                ))
                .collect(Collectors.toList());

        CreateOrderCommand command = new CreateOrderCommand(userEmail, shippingAddress, itemCommands);
        CreateOrderResult result = orderService.createOrder(command);
        return new OrderingResult(result.orderId(), result.orderNumber());
    }
}

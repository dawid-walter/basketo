package com.dwalter.basketo.modules.ordering.infrastructure.adapters;

import com.dwalter.basketo.modules.ordering.application.OrderApplicationService;
import com.dwalter.basketo.modules.ordering.domain.model.Order;
import com.dwalter.basketo.modules.ordering.domain.model.OrderItem;
import com.dwalter.basketo.modules.ordering.domain.model.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
class OrderController {

    private final OrderApplicationService orderService;

    @GetMapping
    public List<OrderResponse> getOrders(HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        // Optional: validate if email is not null (though filter ensures it)

        return orderService.getUserOrders(email).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/by-number/{orderNumber}")
    public OrderResponse getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        ShippingAddressDto shippingDto = null;
        if (order.getShippingAddress() != null) {
            var shipping = order.getShippingAddress();
            shippingDto = new ShippingAddressDto(
                    shipping.firstName(),
                    shipping.lastName(),
                    shipping.addressLine(),
                    shipping.city(),
                    shipping.postalCode(),
                    shipping.country(),
                    shipping.phone()
            );
        }

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUserEmail(),
                shippingDto,
                order.getStatus(),
                order.totalAmount(),
                order.currency(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(item -> new OrderItemDto(item.productName(), item.quantity(), item.unitPrice()))
                        .collect(Collectors.toList())
        );
    }

    record OrderResponse(
            UUID id,
            String orderNumber,
            String userEmail,
            ShippingAddressDto shippingAddress,
            OrderStatus status,
            BigDecimal totalAmount,
            String currency,
            Instant createdAt,
            List<OrderItemDto> items
    ) {}

    record ShippingAddressDto(
            String firstName,
            String lastName,
            String addressLine,
            String city,
            String postalCode,
            String country,
            String phone
    ) {}

    record OrderItemDto(String productName, int quantity, BigDecimal unitPrice) {}
}

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

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.totalAmount(),
                order.currency(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(item -> new OrderItemDto(item.productName(), item.quantity(), item.unitPrice()))
                        .collect(Collectors.toList())
        );
    }

    record OrderResponse(UUID id, OrderStatus status, BigDecimal totalAmount, String currency, Instant createdAt, List<OrderItemDto> items) {}
    record OrderItemDto(String productName, int quantity, BigDecimal unitPrice) {}
}

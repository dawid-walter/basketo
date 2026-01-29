package com.dwalter.basketo.modules.cart.infrastructure.adapters;

import com.dwalter.basketo.modules.cart.application.CartApplicationService;
import com.dwalter.basketo.modules.cart.application.CartApplicationService.CartItemCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
class CartController {

    private final CartApplicationService cartService;

    @PostMapping("/init")
    public ResponseEntity<InitCartResponse> initCart(@Valid @RequestBody InitCartRequest request) {
        List<CartItemCommand> commands = request.items.stream()
                .map(item -> new CartItemCommand(
                        item.productId(),
                        item.productName(),
                        item.quantity(),
                        item.price(),
                        item.currency()
                ))
                .collect(Collectors.toList());

        UUID cartId = cartService.initializeCart(commands, request.userEmail());
        
        return ResponseEntity.ok(new InitCartResponse(cartId));
    }

    @PostMapping("/{cartId}/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@PathVariable UUID cartId) {
        UUID orderId = cartService.checkoutCart(cartId);
        return ResponseEntity.ok(new CheckoutResponse(orderId));
    }

    record InitCartRequest(
            @NotBlank @Email String userEmail,
            @NotEmpty List<@Valid CartItemDto> items
    ) {}

    record CartItemDto(
            @NotNull UUID productId,
            @NotBlank String productName,
            @Positive int quantity,
            @NotNull @Positive BigDecimal price,
            @NotBlank String currency
    ) {}

    record InitCartResponse(UUID cartId) {}
    record CheckoutResponse(UUID orderId) {}
}

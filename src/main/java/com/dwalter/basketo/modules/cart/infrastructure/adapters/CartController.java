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
    public ResponseEntity<CheckoutResponse> checkout(
            @PathVariable UUID cartId,
            @Valid @RequestBody CheckoutRequest request) {

        var shippingAddress = new com.dwalter.basketo.modules.ordering.domain.model.ShippingAddress(
                request.shippingAddress().firstName(),
                request.shippingAddress().lastName(),
                request.shippingAddress().addressLine(),
                request.shippingAddress().city(),
                request.shippingAddress().postalCode(),
                request.shippingAddress().country(),
                request.shippingAddress().phone()
        );

        var result = cartService.checkoutCart(cartId, shippingAddress);
        return ResponseEntity.ok(new CheckoutResponse(result.orderId(), result.orderNumber()));
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

    record CheckoutRequest(@Valid ShippingAddressDto shippingAddress) {}

    record ShippingAddressDto(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String addressLine,
            @NotBlank String city,
            @NotBlank String postalCode,
            @NotBlank String country,
            @NotBlank String phone
    ) {}

    record CheckoutResponse(UUID orderId, String orderNumber) {}
}

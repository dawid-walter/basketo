package com.dwalter.basketo.modules.cart.infrastructure.adapters;

import com.dwalter.basketo.modules.cart.application.CartApplicationService;
import com.dwalter.basketo.modules.cart.application.CartApplicationService.CartItemCommand;
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
    public ResponseEntity<InitCartResponse> initCart(@RequestBody InitCartRequest request) {
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

    record InitCartRequest(String userEmail, List<CartItemDto> items) {}
    record CartItemDto(UUID productId, String productName, int quantity, BigDecimal price, String currency) {}
    record InitCartResponse(UUID cartId) {}
}

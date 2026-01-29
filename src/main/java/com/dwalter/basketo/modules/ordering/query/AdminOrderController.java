package com.dwalter.basketo.modules.ordering.query;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
class AdminOrderController {

    private final OrderViewRepository repository;

    @GetMapping
    public ResponseEntity<List<OrderViewJpaEntity>> getAllOrders(HttpServletRequest request) {
        String role = (String) request.getAttribute("userRole");
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(repository.findAll());
    }
}

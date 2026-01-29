package com.dwalter.basketo.modules.ordering.domain.ports;

import com.dwalter.basketo.modules.ordering.domain.model.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(UUID id);
}

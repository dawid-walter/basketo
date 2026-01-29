package com.dwalter.basketo.modules.ordering.infrastructure.adapters;

import com.dwalter.basketo.modules.ordering.domain.model.Order;
import com.dwalter.basketo.modules.ordering.domain.model.OrderItem;
import com.dwalter.basketo.modules.ordering.domain.ports.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class JpaOrderRepositoryAdapter implements OrderRepository {
    private final SpringDataOrderRepository repository;

    @Override
    public void save(Order order) {
        OrderJpaEntity entity = mapToJpa(order);
        repository.save(entity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    private OrderJpaEntity mapToJpa(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(order.getId(), order.getUserEmail(), order.getStatus(), order.getCreatedAt());
        List<OrderItemJpaEntity> items = order.getItems().stream()
                .map(item -> new OrderItemJpaEntity(
                        null,
                        item.productId(),
                        item.productName(),
                        item.quantity(),
                        item.unitPrice(),
                        item.currency(),
                        entity
                ))
                .collect(Collectors.toList());
        entity.setItems(items);
        return entity;
    }

    private Order mapToDomain(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getCurrency()
                ))
                .collect(Collectors.toList());
        return Order.restore(entity.getId(), entity.getUserEmail(), items, entity.getStatus(), entity.getCreatedAt());
    }
}

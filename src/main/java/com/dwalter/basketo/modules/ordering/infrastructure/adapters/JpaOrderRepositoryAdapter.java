package com.dwalter.basketo.modules.ordering.infrastructure.adapters;

import com.dwalter.basketo.modules.ordering.domain.model.Order;
import com.dwalter.basketo.modules.ordering.domain.model.OrderItem;
import com.dwalter.basketo.modules.ordering.domain.model.ShippingAddress;
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

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return repository.findByOrderNumber(orderNumber).map(this::mapToDomain);
    }

    @Override
    public List<Order> findByUserEmail(String email) {
        return repository.findByUserEmail(email).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private OrderJpaEntity mapToJpa(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(order.getId(), order.getOrderNumber(), order.getUserEmail(), order.getStatus(), order.getCreatedAt());

        // Map shipping address
        ShippingAddress shipping = order.getShippingAddress();
        if (shipping != null) {
            entity.setShippingFirstName(shipping.firstName());
            entity.setShippingLastName(shipping.lastName());
            entity.setShippingAddressLine(shipping.addressLine());
            entity.setShippingCity(shipping.city());
            entity.setShippingPostalCode(shipping.postalCode());
            entity.setShippingCountry(shipping.country());
            entity.setShippingPhone(shipping.phone());
        }

        List<OrderItemJpaEntity> items = order.getItems().stream()
                .map(item -> new OrderItemJpaEntity(
                        UUID.randomUUID(),
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

        // Map shipping address
        ShippingAddress shippingAddress = null;
        if (entity.getShippingFirstName() != null) {
            shippingAddress = new ShippingAddress(
                    entity.getShippingFirstName(),
                    entity.getShippingLastName(),
                    entity.getShippingAddressLine(),
                    entity.getShippingCity(),
                    entity.getShippingPostalCode(),
                    entity.getShippingCountry(),
                    entity.getShippingPhone()
            );
        }

        return Order.restore(entity.getId(), entity.getOrderNumber(), entity.getUserEmail(), shippingAddress, items, entity.getStatus(), entity.getCreatedAt());
    }
}

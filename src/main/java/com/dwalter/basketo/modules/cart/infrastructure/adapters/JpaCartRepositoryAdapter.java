package com.dwalter.basketo.modules.cart.infrastructure.adapters;

import com.dwalter.basketo.modules.cart.domain.model.Cart;
import com.dwalter.basketo.modules.cart.domain.model.CartItem;
import com.dwalter.basketo.modules.cart.domain.model.Price;
import com.dwalter.basketo.modules.cart.domain.ports.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class JpaCartRepositoryAdapter implements CartRepository {

    private final SpringDataCartRepository repository;

    @Override
    public void save(Cart cart) {
        CartJpaEntity entity = mapToJpa(cart);
        repository.save(entity);
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    private CartJpaEntity mapToJpa(Cart cart) {
        CartJpaEntity entity = new CartJpaEntity(cart.getId(), cart.getUserEmail());
        List<CartItemJpaEntity> itemEntities = cart.getItems().stream()
                .map(item -> new CartItemJpaEntity(
                        UUID.randomUUID(),
                        item.productId(),
                        item.productName(),
                        item.quantity(),
                        item.unitPrice().value(),
                        item.unitPrice().currency(),
                        entity
                ))
                .collect(Collectors.toList());
        entity.setItems(itemEntities);
        return entity;
    }

    private Cart mapToDomain(CartJpaEntity entity) {
        List<CartItem> items = entity.getItems().stream()
                .map(item -> new CartItem(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        new Price(item.getPriceAmount(), item.getCurrency())
                ))
                .collect(Collectors.toList());
        return Cart.restore(entity.getId(), entity.getUserEmail(), items);
    }
}

package com.dwalter.basketo.modules.cart.domain.model;

import com.dwalter.basketo.modules.cart.domain.events.CartCheckedOutEvent;
import com.dwalter.basketo.shared.domain.AggregateRoot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Cart extends AggregateRoot {
    private final UUID id;
    private final List<CartItem> items = new ArrayList<>();
    private String userEmail;

    public Cart(UUID id) {
        this.id = id;
    }

    public void checkout(UUID orderId) {
        // Logic to clear items or mark status could go here
        // For now, we just emit the event
        registerEvent(new CartCheckedOutEvent(this.id, orderId));
    }

    public void addItems(List<CartItem> newItems) {
        this.items.addAll(newItems);
    }

    public void assignUser(String email) {
        this.userEmail = email;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public UUID getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    // Restore from Persistence
    public static Cart restore(UUID id, String userEmail, List<CartItem> items) {
        Cart cart = new Cart(id);
        cart.userEmail = userEmail;
        cart.items.addAll(items);
        return cart;
    }
}
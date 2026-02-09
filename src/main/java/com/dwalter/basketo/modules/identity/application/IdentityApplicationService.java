package com.dwalter.basketo.modules.identity.application;

import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.modules.identity.domain.model.User;
import com.dwalter.basketo.modules.identity.domain.ports.PinHasher;
import com.dwalter.basketo.modules.identity.domain.ports.UserRepository;
import com.dwalter.basketo.modules.ordering.domain.model.Order;
import com.dwalter.basketo.modules.ordering.domain.ports.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class IdentityApplicationService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PinHasher pinHasher;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    /**
     * Ensures user exists in the database (find-or-create).
     * Does NOT generate PIN or send email - used when creating orders.
     */
    @Transactional
    public void ensureUserExists(String emailValue) {
        Email email = new Email(emailValue);

        userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.register(email, clock);
                    userRepository.save(newUser);
                    // Note: UserRegisteredEvent is emitted but no PIN is generated
                    newUser.getDomainEvents().forEach(eventPublisher::publishEvent);
                    newUser.clearDomainEvents();
                    return newUser;
                });
    }

    /**
     * Requests a login PIN to be sent to the user's email.
     * Generates a new PIN and triggers PinGeneratedEvent which sends the email.
     * Used when user wants to log in to view their orders.
     */
    @Transactional
    public void requestLoginPin(String emailValue) {
        Email email = new Email(emailValue);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.register(email, clock);
                    userRepository.save(newUser);
                    return newUser;
                });

        user.generateNewPin(pinHasher, clock);
        userRepository.save(user);

        user.getDomainEvents().forEach(eventPublisher::publishEvent);
        user.clearDomainEvents();
    }

    @Transactional
    public void requestLoginPinByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNumber));

        requestLoginPin(order.getUserEmail());
    }

    public boolean verifyPin(String emailValue, String pinCode) {
        Email email = new Email(emailValue);
        return userRepository.findByEmail(email)
                .map(user -> user.verifyPin(pinCode, pinHasher, clock))
                .orElse(false);
    }
}

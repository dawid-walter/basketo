package com.dwalter.basketo.modules.identity.application;

import com.dwalter.basketo.modules.identity.domain.events.PinGeneratedEvent;
import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.modules.identity.domain.model.User;
import com.dwalter.basketo.modules.identity.domain.ports.NotificationSender;
import com.dwalter.basketo.modules.identity.domain.ports.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdentityApplicationService {
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;

    @Transactional
    public void requestLoginPin(String emailValue) {
        Email email = new Email(emailValue);
        
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.register(email);
                    userRepository.save(newUser);
                    return newUser;
                });

        user.generateNewPin();
        userRepository.save(user);

        user.getDomainEvents().stream()
                .filter(PinGeneratedEvent.class::isInstance)
                .map(PinGeneratedEvent.class::cast)
                .forEach(event -> notificationSender.sendPin(event.email(), event.pin()));
        
        user.clearDomainEvents();
    }

    public boolean verifyPin(String emailValue, String pinCode) {
        Email email = new Email(emailValue);
        return userRepository.findByEmail(email)
                .map(user -> user.verifyPin(pinCode))
                .orElse(false);
    }
}

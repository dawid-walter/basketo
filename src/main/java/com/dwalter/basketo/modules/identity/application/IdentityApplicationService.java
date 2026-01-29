package com.dwalter.basketo.modules.identity.application;

import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.modules.identity.domain.model.User;
import com.dwalter.basketo.modules.identity.domain.ports.PinHasher;
import com.dwalter.basketo.modules.identity.domain.ports.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class IdentityApplicationService {
    private final UserRepository userRepository;
    private final PinHasher pinHasher;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

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

    public boolean verifyPin(String emailValue, String pinCode) {
        Email email = new Email(emailValue);
        return userRepository.findByEmail(email)
                .map(user -> user.verifyPin(pinCode, pinHasher, clock))
                .orElse(false);
    }
}

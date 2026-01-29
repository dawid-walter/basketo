package com.dwalter.basketo.modules.identity.infrastructure.adapters;

import com.dwalter.basketo.modules.identity.domain.ports.PinHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BCryptPinHasherAdapter implements PinHasher {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String hash(String rawPin) {
        return passwordEncoder.encode(rawPin);
    }

    @Override
    public boolean matches(String rawPin, String hashedPin) {
        return passwordEncoder.matches(rawPin, hashedPin);
    }
}

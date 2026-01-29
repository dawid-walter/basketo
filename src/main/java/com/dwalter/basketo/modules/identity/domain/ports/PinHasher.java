package com.dwalter.basketo.modules.identity.domain.ports;

public interface PinHasher {
    String hash(String rawPin);
    boolean matches(String rawPin, String hashedPin);
}

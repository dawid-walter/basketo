package com.dwalter.basketo.modules.identity.infrastructure.adapters;

import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.modules.identity.domain.ports.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class FakeEmailAdapter implements NotificationSender {

    @Override
    public void sendPin(Email email, String pin) {
        log.info("------------------------------------------------");
        log.info("SENDING EMAIL TO: {}", email.value());
        log.info("YOUR LOGIN PIN: {}", pin);
        log.info("------------------------------------------------");
    }
}

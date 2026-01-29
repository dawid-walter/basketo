package com.dwalter.basketo.modules.identity.domain.ports;

import com.dwalter.basketo.modules.identity.domain.model.Email;

public interface NotificationSender {
    void sendPin(Email email, String pin);
}

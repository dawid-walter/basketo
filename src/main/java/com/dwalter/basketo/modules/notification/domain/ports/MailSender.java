package com.dwalter.basketo.modules.notification.domain.ports;

public interface MailSender {
    void send(String to, String subject, String body);
}

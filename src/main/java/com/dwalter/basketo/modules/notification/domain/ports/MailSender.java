package com.dwalter.basketo.modules.notification.domain.ports;

import java.util.Map;

public interface MailSender {
    void send(String to, String subject, String body);
    void sendHtml(String to, String subject, String templateName, Map<String, Object> context);
}

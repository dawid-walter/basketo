package com.dwalter.basketo.modules.notification.infrastructure.adapters;

import com.dwalter.basketo.modules.notification.domain.ports.MailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class ConsoleMailSenderAdapter implements MailSender {
    @Override
    public void send(String to, String subject, String body) {
        log.info("📧 EMAIL TO: {}", to);
        log.info("📋 SUBJECT: {}", subject);
        log.info("📝 BODY: {}", body);
        log.info("------------------------------------------------");
    }
}

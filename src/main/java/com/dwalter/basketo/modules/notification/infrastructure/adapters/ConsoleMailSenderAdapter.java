package com.dwalter.basketo.modules.notification.infrastructure.adapters;

import com.dwalter.basketo.modules.notification.domain.ports.MailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.mail.mode", havingValue = "console", matchIfMissing = true)
class ConsoleMailSenderAdapter implements MailSender {

    private final TemplateEngine templateEngine;

    @Override
    public void send(String to, String subject, String body) {
        log.info("📧 EMAIL TO: {}", to);
        log.info("📋 SUBJECT: {}", subject);
        log.info("📝 BODY: {}", body);
        log.info("------------------------------------------------");
    }

    @Override
    public void sendHtml(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process("mail/" + templateName, context);

        log.info("📧 HTML EMAIL TO: {}", to);
        log.info("📋 SUBJECT: {}", subject);
        log.info("📝 RENDERED HTML:\n{}", htmlContent);
        log.info("------------------------------------------------");
    }
}

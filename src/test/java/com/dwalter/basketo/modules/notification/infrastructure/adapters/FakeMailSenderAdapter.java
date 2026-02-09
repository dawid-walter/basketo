package com.dwalter.basketo.modules.notification.infrastructure.adapters;

import com.dwalter.basketo.modules.notification.domain.ports.MailSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fake mail sender adapter for testing purposes.
 * Stores all sent emails in memory for verification in tests.
 */
@Component
@ConditionalOnProperty(name = "app.mail.mode", havingValue = "fake")
public class FakeMailSenderAdapter implements MailSender {

    private final List<SentEmail> sentEmails = new ArrayList<>();

    /**
     * Get all sent emails
     */
    public List<SentEmail> getSentEmails() {
        return sentEmails;
    }

    @Override
    public void send(String to, String subject, String body) {
        sentEmails.add(new SentEmail(to, subject, body, false, null, null));
    }

    @Override
    public void sendHtml(String to, String subject, String templateName, Map<String, Object> variables) {
        sentEmails.add(new SentEmail(to, subject, null, true, templateName, new HashMap<>(variables)));
    }

    /**
     * Clear all stored emails (useful for cleaning up between tests)
     */
    public void clear() {
        sentEmails.clear();
    }

    /**
     * Get the last sent email
     */
    public SentEmail getLastEmail() {
        if (sentEmails.isEmpty()) {
            return null;
        }
        return sentEmails.get(sentEmails.size() - 1);
    }

    /**
     * Get all emails sent to a specific address
     */
    public List<SentEmail> getEmailsSentTo(String to) {
        return sentEmails.stream()
                .filter(email -> email.to().equals(to))
                .toList();
    }

    /**
     * Record of a sent email
     */
    public record SentEmail(
            String to,
            String subject,
            String body,
            boolean isHtml,
            String templateName,
            Map<String, Object> variables
    ) {}
}

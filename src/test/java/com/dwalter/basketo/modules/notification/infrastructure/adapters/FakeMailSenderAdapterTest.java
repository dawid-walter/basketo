package com.dwalter.basketo.modules.notification.infrastructure.adapters;

import com.dwalter.basketo.modules.notification.domain.ports.MailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for FakeMailSenderAdapter (no Spring context needed)
 */
class FakeMailSenderAdapterTest {

    private FakeMailSenderAdapter fakeMailSender;
    private MailSender mailSender;

    @BeforeEach
    void setUp() {
        fakeMailSender = new FakeMailSenderAdapter();
        mailSender = fakeMailSender;
    }

    @Test
    void shouldSendPlainTextEmail() {
        // given
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test body content";

        // when
        mailSender.send(to, subject, body);

        // then
        assertThat(fakeMailSender.getSentEmails()).hasSize(1);

        FakeMailSenderAdapter.SentEmail email = fakeMailSender.getLastEmail();
        assertThat(email.to()).isEqualTo(to);
        assertThat(email.subject()).isEqualTo(subject);
        assertThat(email.body()).isEqualTo(body);
        assertThat(email.isHtml()).isFalse();
    }

    @Test
    void shouldSendHtmlEmail() {
        // given
        String to = "test@example.com";
        String subject = "Test HTML Subject";
        String templateName = "welcome";
        Map<String, Object> variables = Map.of("name", "John", "code", "123456");

        // when
        mailSender.sendHtml(to, subject, templateName, variables);

        // then
        assertThat(fakeMailSender.getSentEmails()).hasSize(1);

        FakeMailSenderAdapter.SentEmail email = fakeMailSender.getLastEmail();
        assertThat(email.to()).isEqualTo(to);
        assertThat(email.subject()).isEqualTo(subject);
        assertThat(email.isHtml()).isTrue();
        assertThat(email.templateName()).isEqualTo(templateName);
        assertThat(email.variables()).containsEntry("name", "John");
        assertThat(email.variables()).containsEntry("code", "123456");
    }

    @Test
    void shouldFilterEmailsByRecipient() {
        // given
        mailSender.send("user1@example.com", "Subject 1", "Body 1");
        mailSender.send("user2@example.com", "Subject 2", "Body 2");
        mailSender.send("user1@example.com", "Subject 3", "Body 3");

        // when
        var emailsToUser1 = fakeMailSender.getEmailsSentTo("user1@example.com");

        // then
        assertThat(emailsToUser1).hasSize(2);
        assertThat(emailsToUser1)
                .extracting(FakeMailSenderAdapter.SentEmail::subject)
                .containsExactly("Subject 1", "Subject 3");
    }
}

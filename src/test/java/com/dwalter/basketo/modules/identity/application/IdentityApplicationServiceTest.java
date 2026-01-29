package com.dwalter.basketo.modules.identity.application;

import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.modules.identity.domain.model.User;
import com.dwalter.basketo.modules.identity.domain.ports.NotificationSender;
import com.dwalter.basketo.modules.identity.domain.ports.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IdentityApplicationServiceTest {

    private IdentityApplicationService service;
    private InMemoryUserRepository userRepository;
    private FakeNotificationSender notificationSender;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        notificationSender = new FakeNotificationSender();
        service = new IdentityApplicationService(userRepository, notificationSender);
    }

    @Test
    void shouldRegisterUserAndSendPinWhenRequestingLogin() {
        // given
        String email = "test@example.com";

        // when
        service.requestLoginPin(email);

        // then
        assertTrue(userRepository.findByEmail(new Email(email)).isPresent());
        assertNotNull(notificationSender.lastSentPin);
        assertEquals(new Email(email), notificationSender.lastSentEmail);
    }

    @Test
    void shouldVerifyCorrectPin() {
        // given
        String email = "test@example.com";
        service.requestLoginPin(email);
        String sentPin = notificationSender.lastSentPin;

        // when
        boolean isVerified = service.verifyPin(email, sentPin);

        // then
        assertTrue(isVerified);
    }

    @Test
    void shouldNotVerifyIncorrectPin() {
        // given
        String email = "test@example.com";
        service.requestLoginPin(email);

        // when
        boolean isVerified = service.verifyPin(email, "000000");

        // then
        assertFalse(isVerified);
    }

    // --- Fakes (In-place for simplicity of the test) ---

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<Email, User> users = new HashMap<>();

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(users.get(email));
        }

        @Override
        public void save(User user) {
            users.put(user.getEmail(), user);
        }
    }

    private static class FakeNotificationSender implements NotificationSender {
        String lastSentEmailValue;
        Email lastSentEmail;
        String lastSentPin;

        @Override
        public void sendPin(Email email, String pin) {
            this.lastSentEmail = email;
            this.lastSentPin = pin;
        }
    }
}

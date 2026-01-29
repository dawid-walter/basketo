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

import static org.assertj.core.api.Assertions.assertThat;

class IdentityApplicationServiceTest {

    private IdentityApplicationService service;
    private InMemoryUserRepository userRepository;
    private FakeNotificationSender notificationSender;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        notificationSender = new FakeNotificationSender();
        // Null for JwtUtils as we test logic unrelated to JWT here (or we mock it if needed)
        // Since IdentityApplicationService doesn't depend on JwtUtils (AuthController does), we are fine.
        service = new IdentityApplicationService(userRepository, notificationSender);
    }

    @Test
    void shouldRegisterUserAndSendPinWhenRequestingLogin() {
        // given
        String email = "test@example.com";

        // when
        service.requestLoginPin(email);

        // then
        assertThat(userRepository.findByEmail(new Email(email)))
                .isPresent();
        
        assertThat(notificationSender.lastSentPin)
                .isNotNull()
                .hasSize(6); // PIN is 6 digits
        
        assertThat(notificationSender.lastSentEmail)
                .isEqualTo(new Email(email));
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
        assertThat(isVerified).isTrue();
    }

    @Test
    void shouldNotVerifyIncorrectPin() {
        // given
        String email = "test@example.com";
        service.requestLoginPin(email);

        // when
        boolean isVerified = service.verifyPin(email, "000000");

        // then
        assertThat(isVerified).isFalse();
    }

    // --- Fakes ---

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
        Email lastSentEmail;
        String lastSentPin;

        @Override
        public void sendPin(Email email, String pin) {
            this.lastSentEmail = email;
            this.lastSentPin = pin;
        }
    }
}
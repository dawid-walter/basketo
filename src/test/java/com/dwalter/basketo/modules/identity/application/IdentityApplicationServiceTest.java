package com.dwalter.basketo.modules.identity.application;

import com.dwalter.basketo.modules.identity.domain.events.PinGeneratedEvent;
import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.modules.identity.domain.model.User;
import com.dwalter.basketo.modules.identity.domain.ports.PinHasher;
import com.dwalter.basketo.modules.identity.domain.ports.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class IdentityApplicationServiceTest {

    private IdentityApplicationService service;
    private InMemoryUserRepository userRepository;
    private FakePinHasher pinHasher;
    private FakeEventPublisher eventPublisher;
    private Clock clock;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        pinHasher = new FakePinHasher();
        eventPublisher = new FakeEventPublisher();
        clock = Clock.fixed(Instant.parse("2026-01-01T12:00:00Z"), ZoneId.of("UTC"));
        service = new IdentityApplicationService(userRepository, pinHasher, eventPublisher, clock);
    }

    @Test
    void shouldRegisterUserAndPublishPinGeneratedEventWhenRequestingLogin() {
        // given
        String email = "test@example.com";

        // when
        service.requestLoginPin(email);

        // then
        assertThat(userRepository.findByEmail(new Email(email)))
                .isPresent();
        
        PinGeneratedEvent event = eventPublisher.getLastEvent(PinGeneratedEvent.class);
        assertThat(event).isNotNull();
        assertThat(event.pin()).hasSize(6);
        assertThat(event.email()).isEqualTo(new Email(email));
    }

    @Test
    void shouldVerifyCorrectPin() {
        // given
        String email = "test@example.com";
        service.requestLoginPin(email);
        String sentPin = eventPublisher.getLastEvent(PinGeneratedEvent.class).pin();

        // when
        boolean isVerified = service.verifyPin(email, sentPin);

        // then
        assertThat(isVerified).isTrue();
    }

    @Test
    void shouldNotVerifyExpiredPin() {
        // given
        String email = "test@example.com";
        service.requestLoginPin(email);
        String sentPin = eventPublisher.getLastEvent(PinGeneratedEvent.class).pin();

        // when move time forward 20 minutes (validity is 15)
        Clock futureClock = Clock.fixed(Instant.parse("2026-01-01T12:20:00Z"), ZoneId.of("UTC"));
        IdentityApplicationService futureService = new IdentityApplicationService(userRepository, pinHasher, eventPublisher, futureClock);
        
        boolean isVerified = futureService.verifyPin(email, sentPin);

        // then
        assertThat(isVerified).isFalse();
    }

    // --- Fakes ---

    private static class FakePinHasher implements PinHasher {
        @Override
        public String hash(String rawPin) {
            return "hashed_" + rawPin;
        }

        @Override
        public boolean matches(String rawPin, String hashedPin) {
            return hashedPin.equals("hashed_" + rawPin);
        }
    }

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

    private static class FakeEventPublisher implements ApplicationEventPublisher {
        private final List<Object> events = new ArrayList<>();

        @Override
        public void publishEvent(Object event) {
            events.add(event);
        }

        public <T> T getLastEvent(Class<T> type) {
            return events.stream()
                    .filter(type::isInstance)
                    .map(type::cast)
                    .reduce((first, second) -> second)
                    .orElse(null);
        }
    }
}

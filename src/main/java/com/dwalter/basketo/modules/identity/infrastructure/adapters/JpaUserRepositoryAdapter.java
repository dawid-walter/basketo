package com.dwalter.basketo.modules.identity.infrastructure.adapters;

import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.modules.identity.domain.model.OneTimePin;
import com.dwalter.basketo.modules.identity.domain.model.User;
import com.dwalter.basketo.modules.identity.domain.ports.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository repository;

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository.findByEmail(email.value())
                .map(this::mapToDomain);
    }

    @Override
    public void save(User user) {
        repository.save(mapToJpa(user));
    }

    private User mapToDomain(UserJpaEntity entity) {
        return User.restore(
                entity.getId(),
                new Email(entity.getEmail()),
                entity.getPinCode(),
                entity.getPinExpiresAt()
        );
    }

    private UserJpaEntity mapToJpa(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getEmail().value(),
                user.getHashedPin(),
                user.getPinExpiresAt()
        );
    }
}

package com.dwalter.basketo.modules.identity.domain.ports;

import com.dwalter.basketo.modules.identity.domain.model.Email;
import com.dwalter.basketo.modules.identity.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(Email email);
    void save(User user);
}

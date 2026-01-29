package com.dwalter.basketo.modules.identity.infrastructure.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class AdminInitializer implements CommandLineRunner {

    private final SpringDataAdminUserRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String email = "admin@basketo.com";
        if (adminRepository.findByEmail(email).isEmpty()) {
            log.info("Initializing default admin account: {}", email);
            AdminUserJpaEntity admin = new AdminUserJpaEntity(
                    UUID.randomUUID(),
                    email,
                    passwordEncoder.encode("admin123")
            );
            adminRepository.save(admin);
        }
    }
}

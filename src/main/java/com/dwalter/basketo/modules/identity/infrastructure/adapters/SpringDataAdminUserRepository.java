package com.dwalter.basketo.modules.identity.infrastructure.adapters;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

interface SpringDataAdminUserRepository extends JpaRepository<AdminUserJpaEntity, UUID> {
    Optional<AdminUserJpaEntity> findByEmail(String email);
}

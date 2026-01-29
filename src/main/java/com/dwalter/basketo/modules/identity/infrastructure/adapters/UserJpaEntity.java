package com.dwalter.basketo.modules.identity.infrastructure.adapters;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class UserJpaEntity {
    @Id
    private UUID id;
    private String email;
    private String pinCode;
    private Instant pinExpiresAt;
}

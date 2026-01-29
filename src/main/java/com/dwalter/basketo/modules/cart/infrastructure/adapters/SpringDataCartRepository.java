package com.dwalter.basketo.modules.cart.infrastructure.adapters;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

interface SpringDataCartRepository extends JpaRepository<CartJpaEntity, UUID> {
}

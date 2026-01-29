package com.dwalter.basketo.modules.ordering.infrastructure.adapters;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {
}

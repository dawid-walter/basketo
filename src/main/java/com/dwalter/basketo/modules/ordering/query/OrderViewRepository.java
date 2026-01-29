package com.dwalter.basketo.modules.ordering.query;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

interface OrderViewRepository extends JpaRepository<OrderViewJpaEntity, UUID> {
}

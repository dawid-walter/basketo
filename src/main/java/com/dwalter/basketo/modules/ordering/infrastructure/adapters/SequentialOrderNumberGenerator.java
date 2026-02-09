package com.dwalter.basketo.modules.ordering.infrastructure.adapters;

import com.dwalter.basketo.modules.ordering.domain.ports.OrderNumberGenerator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
class SequentialOrderNumberGenerator implements OrderNumberGenerator {

    private final JdbcTemplate jdbcTemplate;

    public SequentialOrderNumberGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String generateOrderNumber() {
        Long nextVal = jdbcTemplate.queryForObject(
                "SELECT nextval('order_number_seq')",
                Long.class
        );
        return String.format("ORDER-%06d", nextVal);
    }
}

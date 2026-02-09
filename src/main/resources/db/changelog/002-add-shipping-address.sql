
--liquibase formatted sql

--changeset dwalter:007-add-order-number-sequence
CREATE SEQUENCE IF NOT EXISTS order_number_seq START WITH 100000;

--changeset dwalter:008-add-order-number-and-shipping-address
ALTER TABLE orders
    ADD COLUMN order_number VARCHAR(50) UNIQUE NOT NULL,
    ADD COLUMN shipping_first_name VARCHAR(100),
    ADD COLUMN shipping_last_name VARCHAR(100),
    ADD COLUMN shipping_address_line VARCHAR(255),
    ADD COLUMN shipping_city VARCHAR(100),
    ADD COLUMN shipping_postal_code VARCHAR(20),
    ADD COLUMN shipping_country VARCHAR(100),
    ADD COLUMN shipping_phone VARCHAR(20);

--changeset dwalter:009-create-order-number-index
CREATE INDEX idx_orders_order_number ON orders(order_number);


--liquibase formatted sql

--changeset dwalter:001-create-users-table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    hashed_pin VARCHAR(255),
    pin_expires_at TIMESTAMP
);

--changeset dwalter:002-create-carts-tables
CREATE TABLE carts (
    id UUID PRIMARY KEY,
    user_email VARCHAR(255)
);

CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID REFERENCES carts(id),
    product_id UUID,
    product_name VARCHAR(255),
    quantity INTEGER NOT NULL,
    price_amount DECIMAL(19, 2),
    currency VARCHAR(3)
);

--changeset dwalter:003-create-orders-tables
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_email VARCHAR(255),
    status VARCHAR(50),
    created_at TIMESTAMP
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES orders(id),
    product_id UUID,
    product_name VARCHAR(255),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(19, 2),
    currency VARCHAR(3)
);

--changeset dwalter:004-create-payments-table
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    order_id UUID,
    amount DECIMAL(19, 2),
    currency VARCHAR(3),
    status VARCHAR(50),
    created_at TIMESTAMP
);

--changeset dwalter:005-create-admin-users-table
CREATE TABLE admin_users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255)
);

--changeset dwalter:006-create-order-views-table
CREATE TABLE order_views (
    id UUID PRIMARY KEY,
    user_email VARCHAR(255),
    total_amount DECIMAL(19, 2),
    currency VARCHAR(3),
    status VARCHAR(50),
    created_at TIMESTAMP,
    items_summary TEXT
);

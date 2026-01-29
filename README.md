# Basketo - Modular Monolith Headless E-commerce

Basketo is a backend service providing a streamlined shopping cart and checkout experience for web plugins. It is built using **Clean Architecture**, **Hexagonal Architecture**, and **Domain-Driven Design (DDD)** principles.

## Architecture Highlights
- **Modular Monolith**: Organized into independent modules (`identity`, `cart`, `ordering`, `payment`).
- **Hexagonal (Ports & Adapters)**: Business logic is decoupled from frameworks and infrastructure.
- **Event-Driven**: Modules communicate via domain events for loose coupling.
- **Java 25 & Spring Boot 4.x**: Utilizing the latest Java features and Spring stack.

## Core Flow
1. **Initialize Cart**: Plugin sends shopping items and user email.
2. **Checkout**: User initiates checkout; an Order is created.
3. **Identity**: System automatically creates a temporary account and sends a 6-digit PIN via email (logged to console in dev).
4. **Payment**: System initiates a payment session (mocked with a URL in logs).
5. **Completion**: A payment webhook updates the order status to `PAID`.

## Modules Description
- **Cart**: Handles temporary shopping sessions.
- **Ordering**: Manages order lifecycle and status.
- **Identity**: Handles temporary accounts and PIN-based authentication.
- **Payment**: Integrates with payment providers (Przelewy24, Stripe).

## API Endpoints

### Cart
- `POST /api/carts/init`: Initialize a cart.
- `POST /api/carts/{cartId}/checkout`: Convert cart to an order.

### Auth (Identity)
- `POST /api/auth/login`: Request a new PIN for an email.
- `POST /api/auth/verify`: Verify a PIN and receive a **JWT Access Token**.

### Ordering
- `GET /api/orders`: Get list of orders for the authenticated user (Requires `Authorization: Bearer <token>`).

### Payment
- `POST /api/payments/webhook/{paymentId}`: Simulate a payment success callback.

## Development Setup

### Requirements
- Java 25
- Docker (for PostgreSQL)

### Configuration
Update `src/main/resources/application.properties` with your database credentials or run:
```bash
docker-compose up -d
```

### Running the App
```bash
./gradlew bootRun
```

### Testing the Flow (example using curl)
1. **Init Cart**:
   ```bash
   curl -X POST http://localhost:8080/api/carts/init -H "Content-Type: application/json" -d '{"userEmail": "test@example.com", "items": [{"productId": "550e8400-e29b-41d4-a716-446655440000", "productName": "Laptop", "quantity": 1, "price": 5000, "currency": "PLN"}]}'
   ```
2. **Checkout**:
   ```bash
   curl -X POST http://localhost:8080/api/carts/{CART_ID}/checkout
   ```
3. **Get PIN & Verify**: Check logs for PIN.
   ```bash
   curl -X POST http://localhost:8080/api/auth/verify -H "Content-Type: application/json" -d '{"email": "test@example.com", "pin": "123456"}'
   # Response: {"success":true, "accessToken":"eyJhbGciOi..."}
   ```
4. **Check Orders**:
   ```bash
   curl -X GET http://localhost:8080/api/orders -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>"
   ```
5. **Complete Payment**:
   ```bash
   curl -X POST http://localhost:8080/api/payments/webhook/{PAYMENT_ID}
   ```

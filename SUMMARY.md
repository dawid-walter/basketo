# Basketo - Project Summary & Roadmap

## 1. Project Overview
**Basketo** is a backend service for a headless e-commerce system designed to integrate with frontend plugins. It handles the entire shopping lifecycle: from cart creation, through order processing and payments, to user authentication via temporary accounts (PIN-based).

The system is built using **Java 25** and **Spring Boot 4.x**, following **Modular Monolith**, **Hexagonal Architecture**, and **Domain-Driven Design (DDD)** principles.

## 2. Architecture & Modules

### Core Principles
- **Hexagonal Architecture (Ports & Adapters)**: Strict separation between Domain, Application, and Infrastructure layers.
- **DDD**: Business logic encapsulated in Aggregates (`Cart`, `Order`, `User`, `Payment`).
- **Event-Driven**: Modules communicate primarily via Domain Events to ensure loose coupling.
- **CQRS**: Separate Write Model (Domain Aggregates) and Read Model (`OrderView` for Admin Panel).

### Modules
1.  **Identity**:
    -   Handles temporary user accounts and Admin users.
    -   Authentication via 6-digit PIN (for users) and Password (for admins).
    -   Secured with **JWT** (access tokens with roles: `ROLE_USER`, `ROLE_ADMIN`).
2.  **Cart**:
    -   Manages shopping sessions.
    -   Converts Cart to Order upon checkout.
3.  **Ordering**:
    -   Manages order lifecycle (`CREATED`, `PAID`, `SHIPPED`, `CANCELLED`).
    -   Implements **CQRS Read Model** for high-performance Admin listings.
4.  **Payment**:
    -   Integrates with payment providers (mocked implementation).
    -   Ensures idempotency (deduplication of payment requests).
    -   Handles webhooks to update order status.
5.  **Notification**:
    -   Centralized module for sending emails (currently logs to console).
    -   Listens to events (`PinGenerated`, `OrderCreated`, `PaymentCompleted`).

## 3. Key Technical Features
-   **Database**: PostgreSQL (prod/dev) & Testcontainers (tests).
-   **Security**: Spring Security + JWT Filter + BCrypt for passwords/PINs.
-   **Observability**: Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`).
-   **Documentation**: OpenAPI / Swagger UI (`http://localhost:8080/swagger-ui.html`).
-   **Deployment**: Docker & Docker Compose support.
-   **Testing**:
    -   **Unit**: JUnit 5 + AssertJ + Mockito.
    -   **Integration**: Testcontainers (PostgreSQL) for end-to-end flows.
    -   **ArchUnit**: Automated architecture verification (1.4.1+ for Java 25 support).

## 4. Current Status
The project is a fully functional MVP (Minimum Viable Product).
-   ✅ API for Cart & Checkout.
-   ✅ Authentication flow (PIN via Email).
-   ✅ Payment simulation flow.
-   ✅ Admin Panel API (Login, List Orders).
-   ✅ 100% Tests passing.

## 5. Future Roadmap (What to do next)

### High Priority
1.  **Email Templates**: Replace `ConsoleMailSenderAdapter` with a real SMTP implementation using **Thymeleaf** templates for professional HTML emails.
2.  **Real Payment Gateway**: Replace `FakePaymentGateway` with Stripe or Przelewy24 integration.
3.  **Frontend/Plugin**: Build the JavaScript plugin that consumers will embed on their websites.

### Architectural Improvements
4.  **Transactional Outbox Pattern**: To guarantee event delivery consistency between Database and Message Bus (RabbitMQ/Kafka if scaling out).
5.  **Audit Log**: Track all changes to Orders and Payments for legal compliance.
6.  **Scheduling**: Add a job to expire old pending orders or cleanup abandoned carts.

### DevOps
7.  **CI/CD Pipeline**: GitHub Actions or Jenkins setup.
8.  **Helm Charts**: For Kubernetes deployment.

## 6. How to Start Development
1.  Start Database: `docker-compose up -d`
2.  Run App: `./gradlew bootRun`
3.  Run Tests: `./gradlew test`
4.  View API: Open `http://localhost:8080/swagger-ui.html`

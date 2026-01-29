# Architecture & Implementation Plan - Basketo

## 1. Project Overview
**Basketo** is a backend service designed to handle the shopping cart and checkout process for web plugins. It operates as a headless e-commerce engine focusing on a streamlined "guest-first" user experience using temporary accounts and PIN-based authentication.

## 2. Architectural Patterns

### Modular Monolith
The system is designed as a single deployable unit (Monolith) but structured internally as distinct, loosely coupled modules. This allows for:
- **Encapsulation**: Each module has its own domain and public API.
- **Maintainability**: Changes in one module do not ripple through the entire system.
- **Scalability**: Easy path to microservices extraction in the future if required.

### Hexagonal Architecture (Ports & Adapters)
Each module follows the Hexagonal Architecture to isolate the Domain logic from external concerns (Frameworks, UI, Database).
- **Core (Domain)**: Pure Java code. Contains Aggregates, Entities, Value Objects, and Domain Events. No dependencies on Spring or DB.
- **Application**: Application Services, Use Cases, Command/Query Handlers. Orchestrates the domain logic.
- **Infrastructure**: Implementations of interfaces (Repositories, External APIs), Controllers, Configuration.

### Domain-Driven Design (DDD)
- **Bounded Contexts**: Each module corresponds roughly to a Bounded Context.
- **Aggregates**: Ensure transactional consistency within boundaries.
- **Domain Events**: Used for communication between modules to ensure decoupling (Eventual Consistency).

## 3. Module Breakdown

### 1. `Cart` (Koszyk)
- **Responsibility**: Handling incoming shopping lists from the frontend plugin.
- **Input**: List of products, quantities, raw prices (from plugin).
- **Output**: Validated cart session.

### 2. `Ordering` (Zamówienia)
- **Responsibility**: Lifecycle of an order (Created, Paid, Shipped, Cancelled).
- **Aggregates**: `Order`.
- **Events**: `OrderCreated`, `OrderPaid`, `OrderCancelled`.

### 3. `Identity` (Tożsamość)
- **Responsibility**: Managing temporary user accounts.
- **Mechanism**: Users are identified by Email. Authentication is handled via a One-Time PIN (OTP).
- **Events**: `UserRegistered`, `PinGenerated`.

### 4. `Payment` (Płatności)
- **Responsibility**: Integration with payment gateways (Przelewy24, Stripe).
- **Logic**: Generating payment links, handling callbacks (webhooks), updating payment status.

### 5. `Notification` (Powiadomienia)
- **Responsibility**: Sending emails/SMS.
- **Logic**: Listening to system events (e.g., `PinGenerated`) and dispatching messages via external providers.

## 4. Internal Module Structure
Standard structure for each module (`com.dwalter.basketo.modules.<module_name>`):

```text
├── domain          # Pure Business Logic
│   ├── model       # Aggregates, Entities, Value Objects
│   ├── events      # Domain Events
│   └── ports       # Interfaces for Repositories/Gateways (Output Ports)
├── application     # Use Cases
│   ├── services    # Application Services
│   └── port        # Incoming Ports (if separating strict API)
└── infrastructure  # Framework & I/O
    ├── adapters    # Rest Controllers, JPA Repositories (Impl), External Clients
    └── config      # Spring Configuration specific to this module
```

## 5. Process Flow (Happy Path)

1.  **Input**: Plugin sends `POST /api/cart` with items.
2.  **Cart**: Validates and forwards to `Ordering` (or `Ordering` takes over immediately depending on exact boundary).
3.  **Ordering**: Creates an `Order` aggregate. Emits `OrderCreatedEvent`.
4.  **Identity**: Listens to `OrderCreatedEvent`. Checks if user (email) exists.
    - If new: Creates temporary account.
    - Generates 6-digit PIN.
    - Emits `PinGeneratedEvent`.
5.  **Notification**: Listens to `PinGeneratedEvent`. Sends Email with PIN to user.
6.  **Payment**: Listens to `OrderCreatedEvent`. Calls Payment Gateway (e.g., Stripe) to create a payment intent. Saves `transactionId`.
7.  **User Action**: User receives email, enters PIN in Plugin -> Logged in (`Identity`).
8.  **User Action**: User clicks "Pay" -> Redirected to Payment Gateway.
9.  **Callback**: Gateway hits `POST /webhooks/payment`. `Payment` module updates status -> emits `OrderPaidEvent`.
10. **Ordering**: Listens to `OrderPaidEvent` -> Updates Order status to `PAID`.

## 6. Technology Stack
- **Language**: Java 25
- **Framework**: Spring Boot 4.0.2 (Data JPA, Web)
- **Database**: PostgreSQL
- **Tooling**: Gradle, Lombok

# Basketo - Stan Projektu (Podsumowanie Sesji)

## 1. Kontekst Projektu
**Basketo** to backend typu "headless" dla systemów e-commerce, współpracujący z pluginami WWW. 
- **Stack**: Java 25, Spring Boot 4.0.2, PostgreSQL, Liquibase, Docker.
- **Architektura**: Modularny Monolit, Architektura Hexagonalna, DDD, CQRS.

## 2. Co zostało zrealizowane (Dzisiaj)

### Moduły i Funkcjonalność
- **Identity**: 
    - Logowanie użytkowników (PIN wysyłany e-mailem).
    - Logowanie administratora (Email + Hasło).
    - Mechanizm ról w JWT (`ROLE_USER`, `ROLE_ADMIN`).
    - **Bezpieczeństwo**: Zahashowano PIN-y i hasła przy użyciu BCrypt.
- **Ordering & CQRS**:
    - Wprowadzono model odczytu (**Read Model**) `OrderView`.
    - Dodano `OrderViewEventListener`, który asynchronicznie aktualizuje widok dla admina po zdarzeniach `OrderCreated` i `OrderPaid`.
- **Notification**:
    - Centralny moduł powiadomień.
    - Wdrożono **Thymeleaf** do renderowania szablonów HTML (`pin-email.html`, `order-confirmation.html`).
- **Persistence**:
    - Wprowadzono **Liquibase** do wersjonowania schematu bazy danych.
    - Zastąpiono `@GeneratedValue` ręcznym generowaniem UUID dla elementów koszyka i zamówienia (`CartItem`, `OrderItem`), aby uniknąć problemów z brakującymi sekwencjami w Postgresie.

### Testy i Refaktoryzacja
- **Determinizm czasu**: Wstrzyknięto `Clock` do wszystkich modułów (Identity, Ordering, Payment), co pozwala na pełną kontrolę nad czasem w testach.
- **Unit Tests**: Przebudowano testy na AssertJ i Mockito (usunięto błędy `UnnecessaryStubbingException`).
- **Integration Tests**: Skonfigurowano Testcontainers z adnotacją `@ServiceConnection` (najnowszy standard Spring Boot).
- **Architecture Tests**: Dodano ArchUnit 1.4.1 (wspierający Javę 25) do pilnowania czystości warstw.

## 3. Gdzie przerwaliśmy (Do zrobienia natychmiast)

### ⚠️ Krytyczny błąd w testach integracyjnych
Testy `BasketoApplicationTests` i `ShoppingFlowIntegrationTest` kończą się błędem: 
`SchemaManagementException: Schema validation: missing table [admin_users]`.
- **Status**: Hibernate w trybie `validate` nie widzi tabeli stworzonej przez Liquibase.
- **Podejrzenie**: 
    1. Liquibase może nie startować poprawnie w kontekście testowym.
    2. Plik `001-initial-schema.sql` może wymagać drobnej poprawki (np. kolejność tabel).
    3. Hibernate skanuje encje szybciej niż Liquibase kończy migrację.

## 4. Dalsza Roadmapa
1. **Naprawa schematu**: Doprowadzenie do zielonych testów integracyjnych (Liquibase vs Hibernate).
2. **Akcje Admina**: Implementacja endpointów `ship()` i `cancel()` w module Ordering.
3. **Prawdziwe Płatności**: Zastąpienie `FakePaymentGateway` integracją ze Stripe/Przelewy24.
4. **Outbox Pattern**: Wdrożenie `outbox_messages` dla gwarantowanej dostarczalności zdarzeń.

## 5. Jak uruchomić
- Baza: `docker-compose up -d`
- Aplikacja: `./gradlew bootRun`
- Testy: `./gradlew test` (wymaga Dockera)
- Admin: `admin@basketo.com` / `admin123`

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

### Build Entire Project
```bash
mvn clean install
```

### Build Specific Service
```bash
cd order-service && mvn clean install
cd payment-service && mvn clean install
cd restaurant-service && mvn clean install
cd customer-service && mvn clean install
```

### Build Single Module
```bash
cd order-service/order-domain/order-domain-core && mvn clean compile
```

### Run Tests
```bash
# Run all tests
mvn test

# Run tests for specific service
cd order-service && mvn test

# Run single test class
mvn test -Dtest=OrderApplicationServiceTest
```

### Run Services
```bash
# Order Service (Port 8181)
cd order-service/order-container && mvn spring-boot:run

# Payment Service (Port 8182)
cd payment-service/payment-container && mvn spring-boot:run

# Restaurant Service
cd restaurant-service/restaurant-container && mvn spring-boot:run

# Customer Service
cd customer-service/customer-container && mvn spring-boot:run
```

### Infrastructure Start-Up
```bash
# Start Zookeeper, Kafka cluster, and Debezium connectors
cd infrastructure/docker-compose && ./start-up.sh

# Shutdown infrastructure
cd infrastructure/docker-compose && ./shutdown.sh
```

## Architecture Overview

### Design Pattern
This is a **Clean Architecture + Hexagonal (Ports & Adapters) + Domain-Driven Design** microservices system implementing the **Saga Pattern** and **Outbox Pattern** with **CDC (Change Data Capture)**.

### Service Structure
Each service follows the same layered module pattern:
```
{service}-service/
├── {service}-domain/
│   ├── {service}-domain-core/        # Pure domain logic (entities, events, value objects)
│   └── {service}-application-service/ # Use cases (command handlers, ports, outbox)
├── {service}-dataaccess/             # JPA repositories & adapters
├── {service}-messaging/              # Kafka listeners & message handlers
└── {service}-container/              # Spring Boot application & configuration
```

### Microservices

**Order Service (Port 8181)**
- Orchestrates the order fulfillment saga
- Manages Order aggregate with OrderItems
- Publishes to: `debezium.order.payment_outbox`, `debezium.order.restaurant_approval_outbox`
- Consumes from: `debezium.payment.order_outbox`, `debezium.restaurant.order_outbox`, `customer`

**Payment Service (Port 8182)**
- Handles payment processing and credit management
- Manages Payment, CreditEntry, and CreditHistory aggregates
- Publishes to: `debezium.payment.order_outbox`
- Consumes from: `debezium.order.payment_outbox`

**Restaurant Service**
- Validates restaurant orders and product availability
- Manages Restaurant and OrderApproval aggregates
- Publishes to: `debezium.restaurant.order_outbox`
- Consumes from: `debezium.order.restaurant_approval_outbox`

**Customer Service**
- Manages customer data and credit limits
- Publishes customer creation events to `customer` topic

### Key Patterns

**Saga Pattern**
- Implements distributed transactions with compensating actions
- `OrderPaymentSaga` and `OrderApprovalSaga` handle multi-step workflows
- Each saga step implements `SagaStep<T>` interface with `process()` and `rollback()` methods
- Saga status transitions: `STARTED → PROCESSING → SUCCEEDED/COMPENSATING → COMPENSATED`

**Outbox Pattern**
- Ensures exactly-once message delivery with database transactionality
- Every Kafka message is paired with outbox table write in same transaction
- Debezium CDC monitors PostgreSQL WAL and publishes changes to Kafka
- Outbox status: `STARTED → COMPLETED/FAILED`

**CDC with Debezium**
- Debezium PostgreSQL connector monitors outbox tables
- Creates replication slots: `order_payment_outbox_slot`, `payment_order_outbox_slot`, etc.
- Publishes database changes to Kafka topics with prefix `debezium.*`
- Operations tracked: `CREATE (c)`, `UPDATE (u)`, `DELETE (d)`

### Domain Model

**Value Objects (common-domain)**
- `OrderId`, `CustomerId`, `RestaurantId`, `ProductId` - UUID-based identifiers
- `Money` - Immutable currency handling
- `OrderStatus`: PENDING, PAID, APPROVED, CANCELLING, CANCELLED
- `PaymentStatus`: COMPLETED, CANCELLED, FAILED
- `OrderApprovalStatus`: APPROVED, REJECTED

**Aggregate Roots**
- Order Service: `Order` (with OrderItems)
- Payment Service: `Payment`, `CreditEntry`, `CreditHistory`
- Restaurant Service: `Restaurant`, `OrderApproval`
- Customer Service: `Customer`

### Event Flow Example: Order Creation

```
1. POST /orders → CreateOrderCommand
2. OrderCreateCommandHandler validates order
3. OrderDomainService.validateAndInitiateOrder()
4. Order state: PENDING
5. OrderCreatedEvent published
6. OrderPaymentOutboxMessage saved (OutboxStatus.STARTED)
7. Debezium captures INSERT → debezium.order.payment_outbox
8. Payment Service processes payment
9. Payment response → debezium.payment.order_outbox
10. OrderPaymentSaga.process() updates Order to PAID
11. OrderApprovalOutboxMessage created
12. Restaurant Service validates & approves
13. Order state: PAID → APPROVED
```

**Failure/Rollback Flow:**
- If payment fails: `OrderPaymentSaga.rollback()` → Order state: CANCELLING → CANCELLED
- If restaurant rejects: `OrderApprovalSaga.rollback()` → compensate payment → Order cancelled

### Infrastructure

**Technology Stack:**
- Java 17+ (using Java 21)
- Spring Boot 3.0.5
- Spring Data JPA with Hibernate
- PostgreSQL 15+ (with WAL enabled for CDC)
- Apache Kafka 3.0.5+ (3-broker cluster)
- Debezium PostgreSQL Connector
- Confluent Schema Registry & Avro serialization
- Lombok 1.18.30 (Java 21 compatible)
- Mockito 5.2.0 for testing

**Kafka Configuration:**
- Bootstrap servers: `localhost:19092, localhost:29092, localhost:39092`
- Schema Registry: `http://localhost:8081`
- 3 partitions, replication factor 3
- Producer: Snappy compression, acks=all, batch-size 16384
- Consumer: Batch listener, 3 concurrent threads, max 500 records

**Database:**
- PostgreSQL with separate schemas: `order`, `payment`, `restaurant`, `customer`
- Connection: `jdbc:postgresql://localhost:5432/postgres?currentSchema={schema}`
- Credentials: postgres/admin (local development)

## Common Issues & Solutions

### Lombok Compatibility
The project requires Lombok 1.18.30+ for Java 21 compatibility. This is configured in the root `pom.xml`:
```xml
<lombok.version>1.18.30</lombok.version>
```

### Debezium Connector Setup
After infrastructure startup, verify connectors are created:
```bash
curl http://localhost:8083/connectors
```
Should show: `order-payment-connector`, `order-restaurant-connector`, `payment-order-connector`, `restaurant-order-connector`

### Testing Endpoints
Example order creation request (see `json-files/order.json`):
```bash
curl -X POST http://localhost:8181/orders \
  -H "Content-Type: application/json" \
  -d @json-files/order.json
```

## Code Conventions

### Package Structure
- `entity/` - Domain entities and aggregate roots
- `event/` - Domain events (immutable)
- `valueobject/` - Value objects (immutable)
- `exception/` - Domain-specific exceptions
- `ports/input/` - Use case interfaces (commands, queries, event listeners)
- `ports/output/` - Repository and event publisher interfaces
- `dto/` - Data transfer objects for API layer
- `mapper/` - Entity ↔ DTO conversions
- `outbox/` - Outbox pattern models and helpers

### Naming Conventions
- Commands: `Create*Command`, `Cancel*Command`
- Handlers: `*CommandHandler`, `*QueryHandler`
- Events: `*CreatedEvent`, `*PaidEvent`, `*CancelledEvent`
- Listeners: `*MessageListener`, `*KafkaListener`
- Repositories: `*Repository` (interface), `*RepositoryImpl` (adapter)

### Transaction Boundaries
- Command handlers are transactional entry points
- Domain events published within same transaction as entity changes
- Outbox messages persisted in same transaction for exactly-once delivery

### Idempotency
- Kafka listeners check saga/outbox status before processing
- `OptimisticLockingFailureException` handling prevents duplicate processing
- Unique constraints on outbox entities ensure no duplicate messages

## Module Dependencies

**Shared Modules:**
- `common-domain` - Shared value objects, base entity classes
- `common-application` - Global exception handlers
- `common-dataaccess` - Base repository interfaces
- `common-messaging` - Kafka configuration
- `infrastructure/saga` - Saga orchestration framework
- `infrastructure/outbox` - Outbox pattern framework
- `infrastructure/kafka` - Kafka producer/consumer setup

**Service Dependencies:**
Each service's application-service module depends on:
- `{service}-domain-core` (same service)
- `common-domain`
- `saga`
- `outbox`

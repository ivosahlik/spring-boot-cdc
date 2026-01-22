# Testcontainers Module

This module provides shared Testcontainers configuration for PostgreSQL integration tests across all microservices.

## Purpose

- Eliminates code duplication in test classes
- Provides consistent database test configuration
- Simplifies integration test setup
- Ensures isolated and reproducible test environments

## Available Container Configurations

### 1. PostgresTestContainer (Generic)
Basic PostgreSQL container without service-specific configuration.

```java
@SpringBootTest
@Testcontainers
public class MyTest implements PostgresTestContainer {
    // Your tests here
}
```

### 2. OrderServicePostgresContainer
Configured for Order Service with:
- Schema: `order`
- Init script: `init-schema.sql` (must be in test resources)
- Enum types properly qualified with schema

```java
@SpringBootTest
public class OrderServiceTest implements OrderServicePostgresContainer {
    // Your tests here
}
```

### 3. PaymentServicePostgresContainer
Configured for Payment Service with:
- Schema: `payment`
- Init script: `init-schema.sql` (must be in test resources)
- Enum types properly qualified with schema

```java
@SpringBootTest
public class PaymentServiceTest implements PaymentServicePostgresContainer {
    // Your tests here
}
```

## Usage

### Step 1: Add Dependency

Add to your `*-container/pom.xml`:

```xml
<dependency>
    <groupId>cz.ivosahlik.ordering.system</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>
```

### Step 2: Implement Interface

Instead of manually configuring Testcontainers:

**Before:**
```java
@SpringBootTest
@Testcontainers
public class MyTest {

    @Container
    private static final PostgreSQLContainer<?> container =
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("postgres")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init-schema.sql");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
            container.getJdbcUrl() + "&currentSchema=order&stringtype=unspecified");
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        // ... more properties
    }

    // tests...
}
```

**After:**
```java
@SpringBootTest
public class MyTest implements OrderServicePostgresContainer {
    // tests...
}
```

### Step 3: Ensure Init Script Exists

Make sure you have `src/test/resources/init-schema.sql` with:
- Schema creation
- Enum types properly qualified (e.g., `"order".order_status`)
- Table definitions
- Test data (optional)

## Configuration Details

### JDBC URL Parameters

All containers include these critical parameters:
- `stringtype=unspecified` - Allows Hibernate `@Enumerated(EnumType.STRING)` to work with PostgreSQL custom enum types
- `currentSchema=<schema>` - Sets the default schema for queries

### Container Settings

- **Image**: `postgres:15-alpine` (lightweight PostgreSQL 15)
- **Username**: `test`
- **Password**: `test`
- **Database**: `postgres`
- **Lifecycle**: Container starts once per test class, reused across test methods

### Dynamic Properties

The interfaces automatically configure:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.datasource.driver-class-name`
- `spring.jpa.properties.hibernate.dialect`

## Examples

### Order Service Test
```java
package cz.ivosahlik.ordering.system.order.service.domain;

import cz.ivosahlik.ordering.system.testcontainers.OrderServicePostgresContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(
    classes = OrderServiceApplication.class,
    properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=never",
        "kafka-consumer-config.auto-startup=false"
    }
)
@Sql(value = {"classpath:sql/test-setup.sql"})
@Sql(value = {"classpath:sql/test-cleanup.sql"}, executionPhase = AFTER_TEST_METHOD)
public class OrderPaymentSagaTest implements OrderServicePostgresContainer {

    @Autowired
    private OrderPaymentSaga orderPaymentSaga;

    @Test
    void testOrderPayment() {
        // Test implementation
    }
}
```

### Payment Service Test
```java
package cz.ivosahlik.ordering.system.payment.service.domain;

import cz.ivosahlik.ordering.system.testcontainers.PaymentServicePostgresContainer;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = PaymentServiceApplication.class,
    properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=never",
        "kafka-consumer-config.auto-startup=false"
    }
)
public class PaymentRequestMessageListenerTest implements PaymentServicePostgresContainer {

    @Autowired
    private PaymentRequestMessageListener listener;

    @Test
    void testPaymentProcessing() {
        // Test implementation
    }
}
```

## Creating New Service Containers

To add a container configuration for a new service:

1. Create a new interface in this module:

```java
package cz.ivosahlik.ordering.system.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public interface RestaurantServicePostgresContainer {

    @Container
    PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("postgres")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init-schema.sql");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
            POSTGRES_CONTAINER.getJdbcUrl() + "&currentSchema=restaurant&stringtype=unspecified");
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect",
            () -> "org.hibernate.dialect.PostgreSQLDialect");
    }
}
```

2. Update service-specific schema name and init script as needed

## Benefits

✅ **DRY Principle** - No duplicate Testcontainers configuration
✅ **Consistency** - All services use the same container settings
✅ **Maintainability** - Update once, apply everywhere
✅ **Type Safety** - Compile-time checking via interfaces
✅ **Flexibility** - Easy to create service-specific configurations
✅ **Performance** - Container reuse across test methods

## Troubleshooting

### Docker Not Available
Ensure Docker is running on your machine. Testcontainers requires Docker to create containers.

### Container Startup Timeout
If tests fail with timeout errors, increase Docker resources or check network connectivity.

### Schema Not Found
Ensure your `init-schema.sql` creates the schema:
```sql
CREATE SCHEMA "order";
```

### Enum Type Errors
Make sure enum types are schema-qualified in SQL:
```sql
CREATE TYPE "order".order_status AS ENUM ('PENDING', 'PAID', 'APPROVED');
```

And referenced with schema in table definitions:
```sql
CREATE TABLE "order".orders (
    order_status "order".order_status NOT NULL
);
```

## Version

Current version: 1.0-SNAPSHOT

## Dependencies

This module includes:
- `spring-boot-starter-test`
- `testcontainers:testcontainers`
- `testcontainers:postgresql`
- `testcontainers:junit-jupiter`

# Testcontainers Setup for OrderPaymentSagaTest

## Overview

The `OrderPaymentSagaTest` class has been configured to use Testcontainers for PostgreSQL, providing an isolated, reproducible test environment without requiring an external PostgreSQL instance.

## Changes Made

### 1. Dependencies Added

#### Root POM (`pom.xml`)
- Added Testcontainers version property: `1.19.3`
- Added Testcontainers BOM to dependency management

#### Order Container POM (`order-service/order-container/pom.xml`)
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

### 2. Test Class Updates

#### OrderPaymentSagaTest.java

Added Testcontainers annotations and configuration:

```java
@Testcontainers
public class OrderPaymentSagaTest {

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer =
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("postgres")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init-schema.sql");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
            postgresqlContainer.getJdbcUrl() + "&currentSchema=order&stringtype=unspecified");
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect",
            () -> "org.hibernate.dialect.PostgreSQLDialect");
    }
}
```

Added Spring Boot test properties to disable Kafka:
```java
@SpringBootTest(
    classes = OrderServiceApplication.class,
    properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=never",
        "kafka-consumer-config.auto-startup=false"
    }
)
```

### 3. Database Schema Fixes

Updated `init-schema.sql` to properly define PostgreSQL custom types in the "order" schema:

```sql
-- Changed from:
CREATE TYPE order_status AS ENUM (...)

-- To:
CREATE TYPE "order".order_status AS ENUM (...)
CREATE TYPE "order".saga_status AS ENUM (...)
CREATE TYPE "order".outbox_status AS ENUM (...)

-- Updated table definitions to use schema-qualified types:
CREATE TABLE "order".orders (
    order_status "order".order_status NOT NULL,
    ...
);
```

### 4. Test Configuration

Created `application-test.yml` for test-specific configuration:
- Disabled SQL auto-initialization
- Disabled Kafka auto-startup
- Reduced logging verbosity for tests

## Key Configuration Parameters

### JDBC URL Parameters

- **`currentSchema=order`**: Sets the default schema to "order"
- **`stringtype=unspecified`**: Critical for PostgreSQL enum type compatibility. Allows Hibernate's `@Enumerated(EnumType.STRING)` to work with PostgreSQL custom enum types

### Testcontainer Configuration

- **Image**: `postgres:15-alpine` - Lightweight PostgreSQL 15 image
- **Init Script**: `init-schema.sql` - Creates schema and tables before tests run
- **Dynamic Properties**: Database connection properties are dynamically set from the container

## How It Works

1. **Container Lifecycle**: Testcontainers starts a PostgreSQL container before the test class runs
2. **Schema Initialization**: The `init-schema.sql` script creates the "order" schema and all required tables
3. **Test Data Setup**: `@Sql` annotations load test data from `OrderPaymentSagaTestSetUp.sql`
4. **Test Execution**: Tests run against the containerized database
5. **Cleanup**: `OrderPaymentSagaTestCleanUp.sql` removes test data after each test
6. **Container Shutdown**: Container is automatically stopped and removed after tests complete

## Benefits

- **Isolation**: Each test run uses a fresh database container
- **Reproducibility**: Tests always run against the same PostgreSQL version and configuration
- **No External Dependencies**: No need to have PostgreSQL installed or running locally
- **CI/CD Friendly**: Works seamlessly in continuous integration environments
- **Parallel Execution**: Different test classes can use different containers without conflicts

## Running the Tests

```bash
# Run just the OrderPaymentSagaTest
cd order-service/order-container
mvn test -Dtest=OrderPaymentSagaTest

# Run all tests in order-container
mvn test
```

## Prerequisites

- Docker must be installed and running on the machine executing the tests
- Sufficient disk space for Docker images (PostgreSQL image is ~80MB compressed)
- Internet connection for first run to download Docker images

## Troubleshooting

### Docker Not Found
If you get "Could not find a valid Docker environment":
- Ensure Docker is installed and running
- Check that your user has permission to access Docker

### Port Conflicts
Testcontainers uses random available ports, so port conflicts are rare. However, if issues occur:
- Check that you have available ports in the ephemeral port range
- Ensure no firewall is blocking container networking

### Slow First Run
The first test execution downloads the PostgreSQL Docker image, which may take a few minutes. Subsequent runs are much faster.

## Performance

- **First Run**: ~60 seconds (includes image download)
- **Subsequent Runs**: ~5-10 seconds per test class

## Future Enhancements

Consider applying Testcontainers to other test classes:
- `OrderApplicationServiceTest`
- Payment service tests
- Restaurant service tests
- Customer service tests

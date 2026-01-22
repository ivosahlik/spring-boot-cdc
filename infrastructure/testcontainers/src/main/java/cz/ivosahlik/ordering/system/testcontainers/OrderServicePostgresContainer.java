package cz.ivosahlik.ordering.system.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * PostgreSQL Testcontainer configuration for Order Service tests.
 * Configures the container with the order schema and initialization script.
 *
 * Usage:
 * <pre>
 * {@code
 * @SpringBootTest
 * @Testcontainers
 * public class OrderServiceTest implements OrderServicePostgresContainer {
 *     // Your tests here
 * }
 * }
 * </pre>
 */
@Testcontainers
public interface OrderServicePostgresContainer {

    @Container
    PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("postgres")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init-schema.sql");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
            POSTGRES_CONTAINER.getJdbcUrl() + "&currentSchema=order&stringtype=unspecified");
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect",
            () -> "org.hibernate.dialect.PostgreSQLDialect");
    }
}

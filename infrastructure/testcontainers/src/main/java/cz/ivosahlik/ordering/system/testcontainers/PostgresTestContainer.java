package cz.ivosahlik.ordering.system.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests that require PostgreSQL database.
 * Provides a shared PostgreSQL container configuration using Testcontainers.
 *
 * Usage:
 * <pre>
 * {@code
 * @SpringBootTest
 * public class MyServiceTest extends PostgresTestContainer {
 *     // Your tests here
 * }
 * }
 * </pre>
 *
 * Or implement the interface:
 * <pre>
 * {@code
 * @SpringBootTest
 * @Testcontainers
 * public class MyServiceTest implements PostgresTestContainer {
 *     // Your tests here
 * }
 * }
 * </pre>
 */
@Testcontainers
public interface PostgresTestContainer {

    @Container
    PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("postgres")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
            POSTGRES_CONTAINER.getJdbcUrl() + "&stringtype=unspecified");
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect",
            () -> "org.hibernate.dialect.PostgreSQLDialect");
    }
}

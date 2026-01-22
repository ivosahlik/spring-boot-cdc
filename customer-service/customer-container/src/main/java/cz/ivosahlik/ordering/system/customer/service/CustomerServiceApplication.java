package cz.ivosahlik.ordering.system.customer.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(basePackages = { "cz.ivosahlik.ordering.system.customer.service.dataaccess", "cz.ivosahlik.ordering.system.dataaccess"})
@EntityScan(basePackages = { "cz.ivosahlik.ordering.system.customer.service.dataaccess", "cz.ivosahlik.ordering.system.dataaccess" })
@SpringBootApplication(scanBasePackages = "cz.ivosahlik.ordering.system")
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}

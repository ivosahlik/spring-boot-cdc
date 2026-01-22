package cz.ivosahlik.ordering.system.restaurant.service.domain;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = { "cz.ivosahlik.ordering.system.restaurant.service.dataaccess", "cz.ivosahlik.ordering.system.dataaccess" })
@EntityScan(basePackages = { "cz.ivosahlik.ordering.system.restaurant.service.dataaccess", "cz.ivosahlik.ordering.system.dataaccess" })
@SpringBootApplication(scanBasePackages = "cz.ivosahlik.ordering.system")
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}

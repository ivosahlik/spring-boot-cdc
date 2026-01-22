package cz.ivosahlik.ordering.system.order.service.domain.ports.output.repository;

import cz.ivosahlik.ordering.system.order.service.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Optional<Customer> findCustomer(UUID customerId);

    Customer save(Customer customer);
}

package cz.ivosahlik.ordering.system.order.service.dataaccess.customer.adapter;

import cz.ivosahlik.ordering.system.order.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import cz.ivosahlik.ordering.system.order.service.dataaccess.customer.repository.CustomerJpaRepository;
import cz.ivosahlik.ordering.system.order.service.domain.entity.Customer;
import cz.ivosahlik.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;

    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository.findById(customerId).map(customerDataAccessMapper::customerEntityToCustomer);
    }

    @Override
    public Customer save(Customer customer) {
        return customerDataAccessMapper.customerEntityToCustomer(
                customerJpaRepository.save(customerDataAccessMapper.customerToCustomerEntity(customer)));
    }
}

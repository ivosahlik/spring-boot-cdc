package cz.ivosahlik.ordering.system.customer.service.domain;

import cz.ivosahlik.ordering.system.customer.service.domain.entity.Customer;
import cz.ivosahlik.ordering.system.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerDomainService {

    CustomerCreatedEvent validateAndInitiateCustomer(Customer customer);

}

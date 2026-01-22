package cz.ivosahlik.ordering.system.customer.service.domain.ports.output.message.publisher;

import cz.ivosahlik.ordering.system.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerMessagePublisher {

    void publish(CustomerCreatedEvent customerCreatedEvent);

}

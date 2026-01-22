package cz.ivosahlik.ordering.system.domain.event.publisher;

import cz.ivosahlik.ordering.system.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}

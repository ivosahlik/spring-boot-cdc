package cz.ivosahlik.ordering.system.payment.service.domain.ports.output.repository;

import cz.ivosahlik.ordering.system.domain.valueobject.CustomerId;
import cz.ivosahlik.ordering.system.payment.service.domain.entity.CreditEntry;

import java.util.Optional;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}

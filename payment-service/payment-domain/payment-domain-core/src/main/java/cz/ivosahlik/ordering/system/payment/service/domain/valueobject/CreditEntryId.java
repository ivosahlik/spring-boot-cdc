package cz.ivosahlik.ordering.system.payment.service.domain.valueobject;

import cz.ivosahlik.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {
    public CreditEntryId(UUID value) {
        super(value);
    }
}

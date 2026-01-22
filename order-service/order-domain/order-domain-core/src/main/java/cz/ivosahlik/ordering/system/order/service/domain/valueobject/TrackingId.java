package cz.ivosahlik.ordering.system.order.service.domain.valueobject;

import cz.ivosahlik.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID value) {
        super(value);
    }
}

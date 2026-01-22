package cz.ivosahlik.ordering.system.order.service.domain.valueobject;

import cz.ivosahlik.ordering.system.domain.valueobject.BaseId;

public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long value) {
        super(value);
    }
}

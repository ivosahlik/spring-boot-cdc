package cz.ivosahlik.ordering.system.restaurant.service.domain.ports.output.repository;

import cz.ivosahlik.ordering.system.restaurant.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {
    OrderApproval save(OrderApproval orderApproval);
}

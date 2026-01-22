package cz.ivosahlik.ordering.system.restaurant.service.domain;

import cz.ivosahlik.ordering.system.restaurant.service.domain.entity.Restaurant;
import cz.ivosahlik.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages);
}

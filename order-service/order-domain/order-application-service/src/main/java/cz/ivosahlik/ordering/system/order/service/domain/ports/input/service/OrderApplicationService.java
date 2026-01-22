package cz.ivosahlik.ordering.system.order.service.domain.ports.input.service;

import cz.ivosahlik.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import cz.ivosahlik.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import cz.ivosahlik.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import cz.ivosahlik.ordering.system.order.service.domain.dto.track.TrackOrderResponse;

import jakarta.validation.Valid;

public interface OrderApplicationService {

    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);
}

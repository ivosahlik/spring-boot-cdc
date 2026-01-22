package cz.ivosahlik.ordering.system.order.service.messaging.mapper;

import cz.ivosahlik.ordering.system.domain.event.payload.RestaurantOrderEventPayload;
import cz.ivosahlik.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import cz.ivosahlik.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import cz.ivosahlik.ordering.system.kafka.order.avro.model.RestaurantOrderStatus;
import cz.ivosahlik.ordering.system.order.service.domain.dto.message.CustomerModel;
import cz.ivosahlik.ordering.system.order.service.domain.dto.message.PaymentResponse;
import cz.ivosahlik.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import cz.ivosahlik.ordering.system.domain.event.payload.OrderApprovalEventPayload;
import cz.ivosahlik.ordering.system.domain.event.payload.PaymentOrderEventPayload;
import cz.ivosahlik.ordering.system.domain.valueobject.OrderApprovalStatus;
import cz.ivosahlik.ordering.system.domain.valueobject.PaymentStatus;
import debezium.payment.order_outbox.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

    public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentOrderEventPayload
                                                                             paymentOrderEventPayload,
                                                                     Value
                                                                             paymentResponseAvroModel) {
        return PaymentResponse.builder()
                .id(paymentResponseAvroModel.getId())
                .sagaId(paymentResponseAvroModel.getSagaId())
                .paymentId(paymentOrderEventPayload.getPaymentId())
                .customerId(paymentOrderEventPayload.getCustomerId())
                .orderId(paymentOrderEventPayload.getOrderId())
                .price(paymentOrderEventPayload.getPrice())
                .createdAt(Instant.parse(paymentResponseAvroModel.getCreatedAt()))
                .paymentStatus(PaymentStatus.valueOf(
                        paymentOrderEventPayload.getPaymentStatus()))
                .failureMessages(paymentOrderEventPayload.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponse
    approvalResponseAvroModelToApprovalResponse(RestaurantOrderEventPayload restaurantOrderEventPayload,
                                                debezium.restaurant.order_outbox.Value restaurantApprovalResponseAvroModel) {
        return RestaurantApprovalResponse.builder()
                .id(restaurantApprovalResponseAvroModel.getId())
                .sagaId(restaurantApprovalResponseAvroModel.getSagaId())
                .restaurantId(restaurantOrderEventPayload.getRestaurantId())
                .orderId(restaurantOrderEventPayload.getOrderId())
                .createdAt(Instant.parse(restaurantApprovalResponseAvroModel.getCreatedAt()))
                .orderApprovalStatus(OrderApprovalStatus.valueOf(
                        restaurantOrderEventPayload.getOrderApprovalStatus()))
                .failureMessages(restaurantOrderEventPayload.getFailureMessages())
                .build();
    }

    public RestaurantApprovalRequestAvroModel
    orderApprovalEventToRestaurantApprovalRequestAvroModel(String sagaId, OrderApprovalEventPayload
            orderApprovalEventPayload) {
        return RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderApprovalEventPayload.getOrderId())
                .setRestaurantId(orderApprovalEventPayload.getRestaurantId())
                .setRestaurantOrderStatus(RestaurantOrderStatus
                        .valueOf(orderApprovalEventPayload.getRestaurantOrderStatus()))
                .setProducts(orderApprovalEventPayload.getProducts().stream().map(orderApprovalEventProduct ->
                        cz.ivosahlik.ordering.system.kafka.order.avro.model.Product.newBuilder()
                                .setId(orderApprovalEventProduct.getId())
                                .setQuantity(orderApprovalEventProduct.getQuantity())
                                .build()).collect(Collectors.toList()))
                .setPrice(orderApprovalEventPayload.getPrice())
                .setCreatedAt(orderApprovalEventPayload.getCreatedAt().toInstant())
                .build();
    }

    public CustomerModel customerAvroModeltoCustomerModel(CustomerAvroModel customerAvroModel) {
        return CustomerModel.builder()
                .id(customerAvroModel.getId())
                .username(customerAvroModel.getUsername())
                .firstName(customerAvroModel.getFirstName())
                .lastName(customerAvroModel.getLastName())
                .build();
    }
}

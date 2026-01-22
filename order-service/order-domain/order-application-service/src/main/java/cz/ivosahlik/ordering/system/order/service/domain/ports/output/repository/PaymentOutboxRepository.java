package cz.ivosahlik.ordering.system.order.service.domain.ports.output.repository;

import cz.ivosahlik.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import cz.ivosahlik.ordering.system.outbox.OutboxStatus;
import cz.ivosahlik.ordering.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentOutboxRepository {

    OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage orderPaymentOutboxMessage);

    Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type,
                                                                                     OutboxStatus outboxStatus,
                                                                                     SagaStatus... sagaStatus);
    Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String type,
                                                                         UUID sagaId,
                                                                         SagaStatus... sagaStatus);
    void deleteByTypeAndOutboxStatusAndSagaStatus(String type,
                                                  OutboxStatus outboxStatus,
                                                  SagaStatus... sagaStatus);
}

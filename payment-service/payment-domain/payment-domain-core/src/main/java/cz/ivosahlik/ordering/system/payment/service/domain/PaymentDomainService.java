package cz.ivosahlik.ordering.system.payment.service.domain;

import cz.ivosahlik.ordering.system.payment.service.domain.entity.CreditEntry;
import cz.ivosahlik.ordering.system.payment.service.domain.entity.CreditHistory;
import cz.ivosahlik.ordering.system.payment.service.domain.entity.Payment;
import cz.ivosahlik.ordering.system.payment.service.domain.event.PaymentEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages);

    PaymentEvent validateAndCancelPayment(Payment payment,
                                          CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories,
                                          List<String> failureMessages);
}

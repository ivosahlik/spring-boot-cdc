package cz.ivosahlik.ordering.system.payment.service.dataaccess.credithistory.mapper;

import cz.ivosahlik.ordering.system.domain.valueobject.CustomerId;
import cz.ivosahlik.ordering.system.domain.valueobject.Money;
import cz.ivosahlik.ordering.system.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import cz.ivosahlik.ordering.system.payment.service.domain.entity.CreditHistory;
import cz.ivosahlik.ordering.system.payment.service.domain.valueobject.CreditHistoryId;
import org.springframework.stereotype.Component;

@Component
public class CreditHistoryDataAccessMapper {

    public CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
        return CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(creditHistoryEntity.getId()))
                .customerId(new CustomerId(creditHistoryEntity.getCustomerId()))
                .amount(new Money(creditHistoryEntity.getAmount()))
                .transactionType(creditHistoryEntity.getType())
                .build();
    }

    public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
        return CreditHistoryEntity.builder()
                .id(creditHistory.getId().getValue())
                .customerId(creditHistory.getCustomerId().getValue())
                .amount(creditHistory.getAmount().getAmount())
                .type(creditHistory.getTransactionType())
                .build();
    }

}

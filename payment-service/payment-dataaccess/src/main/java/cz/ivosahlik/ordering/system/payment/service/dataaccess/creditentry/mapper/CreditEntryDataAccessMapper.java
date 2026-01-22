package cz.ivosahlik.ordering.system.payment.service.dataaccess.creditentry.mapper;

import cz.ivosahlik.ordering.system.domain.valueobject.CustomerId;
import cz.ivosahlik.ordering.system.domain.valueobject.Money;
import cz.ivosahlik.ordering.system.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import cz.ivosahlik.ordering.system.payment.service.domain.entity.CreditEntry;
import cz.ivosahlik.ordering.system.payment.service.domain.valueobject.CreditEntryId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataAccessMapper {

    public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity) {
        return CreditEntry.builder()
                .creditEntryId(new CreditEntryId(creditEntryEntity.getId()))
                .customerId(new CustomerId(creditEntryEntity.getCustomerId()))
                .totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
                .build();
    }

    public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
        return CreditEntryEntity.builder()
                .id(creditEntry.getId().getValue())
                .customerId(creditEntry.getCustomerId().getValue())
                .totalCreditAmount(creditEntry.getTotalCreditAmount().getAmount())
                .build();
    }

}

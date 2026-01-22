package cz.ivosahlik.ordering.system.customer.service.domain.exception;

import cz.ivosahlik.ordering.system.domain.exception.DomainException;

public class CustomerDomainException extends DomainException {

    public CustomerDomainException(String message) {
        super(message);
    }
}

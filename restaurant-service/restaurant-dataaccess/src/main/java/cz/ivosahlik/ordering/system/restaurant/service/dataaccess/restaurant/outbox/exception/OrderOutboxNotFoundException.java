package cz.ivosahlik.ordering.system.restaurant.service.dataaccess.restaurant.outbox.exception;

public class OrderOutboxNotFoundException extends RuntimeException {

    public OrderOutboxNotFoundException(String message) {
        super(message);
    }
}

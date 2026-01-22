package cz.ivosahlik.ordering.system.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}

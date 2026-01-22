package cz.ivosahlik.ordering.system.saga;

public enum SagaStatus {
    STARTED,
    FAILED,
    SUCCEEDED,
    PROCESSING,
    COMPENSATING,
    COMPENSATED
}

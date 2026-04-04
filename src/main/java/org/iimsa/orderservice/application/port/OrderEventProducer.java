package org.iimsa.orderservice.application.port;

import org.iimsa.orderservice.domain.events.OrderCreatedEvent;

public interface OrderEventProducer {
    void orderCreatedEvent(String correlationId, String domainType, String domainId, String eventType,
                           OrderCreatedEvent payload);
}

package org.iimsa.orderservice.infrastructure.messaging.kafka.producer;

import org.iimsa.common.event.Events;
import org.iimsa.orderservice.application.port.OrderEventProducer;
import org.iimsa.orderservice.domain.events.OrderCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducerImpl implements OrderEventProducer {
    @Override
    public void orderCreatedEvent(String correlationId, String domainType, String domainId, String eventType,
                                  OrderCreatedEvent payload) {
        Events.trigger(correlationId, domainType, domainId, eventType, payload);
    }
}

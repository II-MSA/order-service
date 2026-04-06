package org.iimsa.orderservice.infrastructure.messaging.kafka.producer;

import org.iimsa.common.event.Events;
import org.iimsa.orderservice.domain.events.OrderEventProducer;
import org.iimsa.orderservice.domain.events.payload.OrderCreatedEvent;
import org.iimsa.orderservice.infrastructure.messaging.kafka.OrderTopicProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(OrderTopicProperties.class) // 프로퍼티 활성화
public class OrderEventProducerImpl implements OrderEventProducer {
    @Override
    public void orderCreatedEvent(String correlationId, String domainType, String domainId, String eventType,
                                  OrderCreatedEvent payload) {
        Events.trigger(correlationId, domainType, domainId, eventType, payload);
    }
}

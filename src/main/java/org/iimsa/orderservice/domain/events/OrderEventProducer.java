package org.iimsa.orderservice.domain.events;

import org.iimsa.orderservice.domain.events.payload.OrderCreatedEvent;

public interface OrderEventProducer {
    // 추적용ID, 도메인, 도메인PK값, 이벤트 행위, payload
    void orderCreatedEvent(String correlationId, String domainType, String domainId, String eventType, String topic,
                           OrderCreatedEvent payload);
}

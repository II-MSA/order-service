package org.iimsa.orderservice.domain.events;

import org.iimsa.orderservice.domain.events.payload.OrderCancelBySystemEvent;

public interface OrderEventConsumer {
    void handleOrderCanceledBySystemFromDelivery(OrderCancelBySystemEvent orderCancelBySystemEvent);

    void handleOrderCanceledBySystemFromHub(OrderCancelBySystemEvent orderCancelBySystemEvent);
}

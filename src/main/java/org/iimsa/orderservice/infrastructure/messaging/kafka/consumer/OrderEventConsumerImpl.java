package org.iimsa.orderservice.infrastructure.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.iimsa.common.messaging.annotation.IdempotentConsumer;
import org.iimsa.orderservice.application.OrderService;
import org.iimsa.orderservice.domain.events.OrderEventConsumer;
import org.iimsa.orderservice.domain.events.payload.OrderCancelBySystemEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class OrderEventConsumerImpl implements OrderEventConsumer {

    private final OrderService orderService; // 응용 계층 호출

    @Transactional
    @IdempotentConsumer("delivery-order-canceled-requested")
    @KafkaListener(
            topics = "${kafka.topics.delivery-cancel:delivery-order-canceled-requested}",
            groupId = "${spring.kafka.consumer.group-id:order-service}"
    )
    public void handleOrderCanceledBySystemFromDelivery(OrderCancelBySystemEvent payload) {
        orderService.cancelOrderBySystem(payload.orderId());
    }

    @Transactional
    @IdempotentConsumer("hub-order-canceled-requested")
    @KafkaListener(
            topics = "${kafka.topics.hub-cancel:hub-order-canceled-requested}",
            groupId = "${spring.kafka.consumer.group-id:order-service}"
    )
    public void handleOrderCanceledBySystemFromHub(OrderCancelBySystemEvent payload) {
        orderService.cancelOrderBySystem(payload.orderId());
    }
}

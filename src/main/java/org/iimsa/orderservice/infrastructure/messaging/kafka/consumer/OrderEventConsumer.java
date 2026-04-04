package org.iimsa.orderservice.infrastructure.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.iimsa.common.messaging.annotation.IdempotentConsumer;
import org.iimsa.orderservice.domain.events.OrderCancelBySystemEvent;
import org.iimsa.orderservice.domain.model.Order;
import org.iimsa.orderservice.domain.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class OrderEventConsumer {

    private final OrderRepository orderRepository;

    @Transactional
    @IdempotentConsumer("delivery-order-canceled-requested")
    @KafkaListener(
            topics = "${kafka.topics.delivery-cancel:delivery-order-canceled-requested}",
            groupId = "${spring.kafka.consumer.group-id:order-service}"
    )
    public void handleOrderCanceledBySystem(OrderCancelBySystemEvent orderCancelBySystemEvent) {
        Order order = orderRepository.findById(orderCancelBySystemEvent.orderId())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 ID의 주문이 없습니다."));

        order.cancelBySystem();
    }
}

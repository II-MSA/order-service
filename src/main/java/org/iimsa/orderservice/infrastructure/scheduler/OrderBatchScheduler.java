package org.iimsa.orderservice.infrastructure.scheduler;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.application.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderBatchScheduler {
    private final OrderService orderService;

    @Scheduled(cron = "0 0 0 * * *")
    public void fixOrderAndPublishEventToHub() {
        String correlationId = UUID.randomUUID().toString();
        orderService.fixOrderAndPublishEventToHub(correlationId);
    }
}

package org.iimsa.orderservice.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.application.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderBatchScheduler {

    private final OrderService orderService;

    // 매일 자정 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void startMidnightDelivery() {
        // 자정에 상태를 배송중으로 바꾸는 메서드 실행
        orderService.startBulkDelivery();
    }
}

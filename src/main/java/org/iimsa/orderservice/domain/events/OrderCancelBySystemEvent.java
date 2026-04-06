package org.iimsa.orderservice.domain.events;

import java.util.UUID;

public record OrderCancelBySystemEvent(
        String correlationId, // 추적용 ID (추천)
        UUID orderId,
        String reason
) {
}

package org.iimsa.orderservice.domain.events;

import java.util.UUID;

public record OrderCreatedEvent(
        String correlationId, // 추적용 ID (추천)
        UUID orderId,
        UUID productId,
        UUID receiverId,
        UUID supplierId,
        UUID deliveryId,
        String requestDetails
) {
}



package org.iimsa.orderservice.domain.events.payload;

import java.util.UUID;

public record OrderCreatedEvent(
        String correlationId,
        UUID orderId,
        UUID productId,
        UUID receiverId,
        UUID supplierId,
        UUID deliveryId,
        String requestDetails
) {
}

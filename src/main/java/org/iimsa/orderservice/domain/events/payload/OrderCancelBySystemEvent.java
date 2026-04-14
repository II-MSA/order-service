package org.iimsa.orderservice.domain.events.payload;

import java.util.UUID;

public record OrderCancelBySystemEvent(
        String correlationId,
        UUID orderId,
        String reason
) {
}

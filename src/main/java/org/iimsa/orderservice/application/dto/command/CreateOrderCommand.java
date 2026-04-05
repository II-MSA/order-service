package org.iimsa.orderservice.application.dto.command;

import java.util.UUID;

public record CreateOrderCommand(
        UUID supplierId,
        UUID receiverId,
        UUID productId,
        Integer quantity,
        String requestDetails
) {
}

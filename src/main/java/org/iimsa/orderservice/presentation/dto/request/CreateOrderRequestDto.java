package org.iimsa.orderservice.presentation.dto.request;

import java.util.UUID;

public record CreateOrderRequestDto(
        UUID supplierId,
        UUID receiverId,
        UUID productId,
        Integer quantity,
        String requestDetails
) {
}

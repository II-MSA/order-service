package org.iimsa.orderservice.presentation.dto.response;

import java.util.UUID;

public record UpdateResponseDto(
        UUID orderId,
        String message
) {
    public static UpdateResponseDto success(UUID orderId, String message) {
        return new UpdateResponseDto(orderId, message);
    }
}

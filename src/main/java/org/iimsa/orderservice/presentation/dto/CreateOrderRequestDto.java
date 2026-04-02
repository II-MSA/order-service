package org.iimsa.orderservice.presentation.dto;

import java.util.UUID;

public record CreateOrderRequestDto(
        UUID supplierId,
        UUID receiverId,
        UUID productId,
        Integer quantity,
        String requestDetails,
        String paymentMethod // "CARD", "CASH" 등 문자열로 받아 서비스에서 Enum 변환
) {}

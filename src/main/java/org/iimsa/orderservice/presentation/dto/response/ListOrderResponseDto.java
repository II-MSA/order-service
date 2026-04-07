package org.iimsa.orderservice.presentation.dto.response;

import java.util.UUID;

public record ListOrderResponseDto(
        UUID orderId,
        UUID productId,
        Integer productQuantity,
        String productName,
        UUID receiverId,
        String receiverName,
        UUID receiverHubId,
        String receiverHubName,
        UUID supplierId,
        String supplierName,
        UUID supplierHubId,
        String supplierHubName,
        UUID deliveryId,
        String requestDetails
) {
}

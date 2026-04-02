package org.iimsa.orderservice.application.dto.command;

import java.util.UUID;
import org.iimsa.orderservice.presentation.dto.CreateOrderRequestDto;

public record CreateOrderCommand(
        UUID supplierId,
        UUID receiverId,
        UUID productId,
        Integer quantity,
        String requestDetails
) {
    public static CreateOrderCommand from(CreateOrderRequestDto request) {
        return new CreateOrderCommand(
                request.supplierId(),
                request.receiverId(),
                request.productId(),
                request.quantity(),
                request.requestDetails()
        );
    }
}

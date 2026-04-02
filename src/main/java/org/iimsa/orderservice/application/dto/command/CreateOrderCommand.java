package org.iimsa.orderservice.application.dto.command;

import java.util.UUID;
import org.iimsa.orderservice.domain.model.PaymentMethod;
import org.iimsa.orderservice.presentation.dto.CreateOrderRequestDto;

public record CreateOrderCommand(
        UUID supplierId,
        UUID receiverId,
        UUID productId,
        Integer quantity,
        String requestDetails,
        PaymentMethod paymentMethod
) {
    public static CreateOrderCommand from(CreateOrderRequestDto request) {
        return new CreateOrderCommand(
                request.supplierId(),
                request.receiverId(),
                request.productId(),
                request.quantity(),
                request.requestDetails(),
                PaymentMethod.valueOf(request.paymentMethod().toUpperCase())
        );
    }
}

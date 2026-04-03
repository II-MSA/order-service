package org.iimsa.orderservice.application.dto.command;

import java.util.UUID;

public record UpdateProductCommand(
        UUID productId,
        Integer quantity
) {
}

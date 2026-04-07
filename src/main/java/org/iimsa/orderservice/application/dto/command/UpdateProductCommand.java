package org.iimsa.orderservice.application.dto.command;

import java.util.Objects;
import java.util.UUID;

public record UpdateProductCommand(
        UUID productId,
        Integer quantity
) {
    public UpdateProductCommand {
        Objects.requireNonNull(quantity, "quantity는 필수입니다.");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
        }
    }
}

package org.iimsa.orderservice.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "주문 생성 응답 DTO")
public record CreateOrderResponseDto(
        @Schema(description = "생성된 주문 ID")
        UUID orderId
) {
}

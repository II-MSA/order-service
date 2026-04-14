package org.iimsa.orderservice.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "주문 생성 요청 DTO")
public record CreateOrderRequestDto(
        @Schema(description = "공급 업체 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID supplierId,

        @Schema(description = "수령 업체 ID", example = "6ba7b810-9dad-11d1-80b4-00c04fd430c8")
        UUID receiverId,

        @Schema(description = "상품 ID", example = "ad27027a-6fca-4434-8c80-928972bcae2f")
        UUID productId,

        @Schema(description = "주문 수량", example = "10")
        Integer quantity,

        @Schema(description = "요청 상세 사항", example = "오전 중 배송 부탁드립니다.")
        String requestDetails
) {
}

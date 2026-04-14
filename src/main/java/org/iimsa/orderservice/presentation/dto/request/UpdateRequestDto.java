package org.iimsa.orderservice.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "주문 수정 요청 DTO")
public record UpdateRequestDto(
        @Schema(description = "수정할 상품 ID")
        UUID productId,

        @Schema(description = "수정할 수량", example = "15")
        Integer quantity,

        @Schema(description = "수령 업체 ID (동기화용)")
        UUID receiverId,

        @Schema(description = "공급 업체 ID (동기화용)")
        UUID supplierId
) {
}

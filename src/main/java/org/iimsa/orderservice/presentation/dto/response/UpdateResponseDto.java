package org.iimsa.orderservice.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "주문 수정 응답 DTO")
public record UpdateResponseDto(
        @Schema(description = "수정된 주문 ID")
        UUID orderId,

        @Schema(description = "결과 메시지", example = "주문이 성공적으로 수정되었습니다.")
        String message
) {
    public static UpdateResponseDto success(UUID orderId, String message) {
        return new UpdateResponseDto(orderId, message);
    }
}

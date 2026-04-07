package org.iimsa.orderservice.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "주문 목록 조회 응답 DTO")
public record ListOrderResponseDto(
        @Schema(description = "주문 ID")
        UUID orderId,

        @Schema(description = "상품 ID")
        UUID productId,

        @Schema(description = "상품 수량")
        Integer productQuantity,

        @Schema(description = "상품명")
        String productName,

        @Schema(description = "수령 업체 ID")
        UUID receiverId,

        @Schema(description = "수령 업체명")
        String receiverName,

        @Schema(description = "수령 허브 ID")
        UUID receiverHubId,

        @Schema(description = "수령 허브명")
        String receiverHubName,

        @Schema(description = "공급 업체 ID")
        UUID supplierId,

        @Schema(description = "공급 업체명")
        String supplierName,

        @Schema(description = "공급 허브 ID")
        UUID supplierHubId,

        @Schema(description = "공급 허브명")
        String supplierHubName,

        @Schema(description = "배송 ID")
        UUID deliveryId,

        @Schema(description = "요청 상세")
        String requestDetails
) {
}

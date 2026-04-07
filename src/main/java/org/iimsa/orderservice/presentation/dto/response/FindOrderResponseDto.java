package org.iimsa.orderservice.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import org.iimsa.orderservice.domain.model.Order;

@Schema(description = "주문 단건 상세 조회 응답 DTO")
public record FindOrderResponseDto(
        @Schema(description = "주문 ID")
        UUID orderId,

        @Schema(description = "상품 ID")
        UUID productId,

        @Schema(description = "배송 ID")
        UUID deliveryId,

        @Schema(description = "주문 상태", example = "PENDING")
        String orderStatus,

        @Schema(description = "요청 상세 내용")
        String requestDetails,

        @Schema(description = "상품명")
        String productName,

        @Schema(description = "수량")
        Integer quantity,

        @Schema(description = "공급 업체명")
        String supplierName,

        @Schema(description = "수령 업체명")
        String receiverName
) {
    public static FindOrderResponseDto from(Order order) {
        return new FindOrderResponseDto(
                order.getId(),
                order.getProduct().getProductId(),
                order.getDeliveryId(),
                order.getOrderStatus().toString(),
                order.getRequestDetails(),
                order.getProduct().getProductName(),
                order.getProduct().getQuantity(),
                order.getSupplier().getSupplierName(),
                order.getReceiver().getReceiverName()
        );
    }
}

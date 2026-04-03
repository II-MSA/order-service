package org.iimsa.orderservice.presentation.dto.response;

import java.util.UUID;
import org.iimsa.orderservice.domain.model.Order;

public record FindOrderResponseDto(
        UUID orderId,
        UUID productId,
        UUID deliveryId,
        String orderStatus,
        String requestDetails,

        // 상품 정보 (Product VO)
        String productName,
        Integer quantity,

        // 공급 업체 정보 (Supplier VO)
        String supplierName,

        // 수령자 정보 (Receiver VO)
        String receiverName
) {
    // Entity -> DTO 변환 정적 팩토리 메서드
    public static FindOrderResponseDto from(Order order) {
        return new FindOrderResponseDto(
                order.getId(),
                order.getProduct().getProductId(),
                order.getDeliveryId(),
                order.getOrderStatus().toString(),
                order.getRequestDetails(),

                // Product (VO 필드명에 맞춰 수정하세요)
                order.getProduct().getProductName(),
                order.getProduct().getQuantity(),

                // Supplier
                order.getSupplier().getSupplierName(),

                // Receiver
                order.getReceiver().getReceiverName()
        );
    }
}

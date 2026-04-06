package org.iimsa.orderservice.presentation.dto.request;

import java.util.UUID;

public record UpdateRequestDto(
        // 상품 수정용
        UUID productId,
        Integer quantity,

        // 업체 정보 동기화용 (필요 시)
        UUID receiverId,
        UUID supplierId
) {
}

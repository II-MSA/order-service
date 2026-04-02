package org.iimsa.orderservice.application.dto.result;

import java.util.UUID;

/**
 * 수령 업체 측에서 보내오는 이벤트 Payload 규격
 */
public record PartnerOrderEvent(
        UUID supplierId,
        UUID receiverId,
        UUID productId,
        Integer quantity,
        String memo
) {}

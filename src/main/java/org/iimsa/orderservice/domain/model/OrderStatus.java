package org.iimsa.orderservice.domain.model;

public enum OrderStatus {
    ORDER_CREATED,          // 주문 생성
    ORDER_CANCELLED,        // 주문 취소 완료
    IN_TRANSIT,             // 배송중 ( 00시 이후 배송중으로 변경 )
    DELIVERED,              // 배송 완료
}

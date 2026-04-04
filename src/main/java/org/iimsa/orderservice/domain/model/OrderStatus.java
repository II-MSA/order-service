package org.iimsa.orderservice.domain.model;

public enum OrderStatus {
    ORDER_CREATED,          // 주문 생성
    ORDER_CANCELLED,        // 주문 취소 완료
    ORDER_FIXED,            // 주문 확정 ( 00시 이후 주문확정으로 변경 )
    SYSTEM_CANCELLED,       // 서비스 운영진이 주문을 취소한 경우
}

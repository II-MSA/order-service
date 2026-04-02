package org.iimsa.orderservice.domain.model;

public enum OrderStatus {
    // 결제 관련
    PENDING_PAYMENT,
    PAID,

    // 배송 관련
    PREPARING_SHIPMENT,
    IN_TRANSIT,
    DELIVERED,

    // 환불 관련
    REFUND_REQUESTED,
    REFUNDED,

    // 주문 취소 관련
    CANCEL_REQUESTED,
    CANCELLED
}

package org.iimsa.orderservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// protected는 같은 패키지만 적용돼서 엔티티랑 VO를 같은 패키지 아래에 둔다.
@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    private Product product;

    @Embedded
    private Supplier supplier;

    @Embedded
    private Receiver receiver;

    @Column(name = "delivery_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "request_details", columnDefinition = "TEXT")
    private String requestDetails;

    // 주문 id와 배달 id는 생성시 자동으로 UUID 생성, 주문 상태는 별도 메서드에서 생성
    @Builder
    public Order(Product product,
                 Supplier supplier,
                 Receiver receiver,
                 OrderStatus orderStatus,
                 String requestDetails) {
        this.product = product;
        this.supplier = supplier;
        this.receiver = receiver;
        this.orderStatus = orderStatus;
        this.requestDetails = requestDetails;
    }

    public static Order create(
            Product product,
            Supplier supplier,
            Receiver receiver,
            String requestDetails
    ) {
        OrderStatus status = OrderStatus.ORDER_CREATED;

        return Order.builder()
                .product(product)
                .supplier(supplier)
                .receiver(receiver)
                .requestDetails(requestDetails)
                .orderStatus(status)
                .build();
    }

    // ===========================
    // 상태 변경 로직
    // ===========================

    // 주문 생성 -> 배송중
    public void startDelivery(UUID deliveryId) {
        validateCurrentStatus(OrderStatus.ORDER_CREATED);
        this.deliveryId = deliveryId;
        this.orderStatus = OrderStatus.IN_TRANSIT;
    }

    // 배송중 -> 수령 업체로 배송이 완료되면 DELIVERED
    public void completeDelivery() {
        validateCurrentStatus(OrderStatus.IN_TRANSIT);
        this.orderStatus = OrderStatus.DELIVERED;
    }

    // 주문 생성 -> 주문 취소 ( 12시 전에만 가능 )
    public void cancel() {
        validateCurrentStatus(OrderStatus.ORDER_CREATED);
        this.orderStatus = OrderStatus.ORDER_CANCELLED;
    }

    // ===========================
    // 내부 검증
    // ===========================
    private void validateCurrentStatus(OrderStatus expected) {
        if (this.orderStatus != expected) {
            throw new IllegalStateException(
                    "현재 상태(" + this.orderStatus + ")에서는 수행할 수 없습니다. 기대 상태: " + expected
            );
        }
    }
}

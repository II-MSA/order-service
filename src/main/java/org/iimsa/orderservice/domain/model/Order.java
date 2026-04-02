package org.iimsa.orderservice.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import org.iimsa.orderservice.domain.service.CompanyProvider;
import org.iimsa.orderservice.domain.service.ProductProvider;

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
            String requestDetails,
            PaymentMethod paymentMethod
    ) {
        // 결제 종류에 따라서 주문 생성시 주문 상태를 정의한다.
        // 무통장 입금의 경우 PENDING_PAYMENT로 생성
        // 카드결제의 경우 바로 PAID로 생성
        OrderStatus status = (paymentMethod == PaymentMethod.CARD)
                ? OrderStatus.PAID
                : OrderStatus.PENDING_PAYMENT;

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

    // 무통장 결제중, 계좌로 입금을 완료하면 상태를 PENDING_PAYMENT -> PAID 로 변경한다.
    public void markAsPaid() {
        validateCurrentStatus(OrderStatus.PENDING_PAYMENT);
        this.orderStatus = OrderStatus.PAID;
    }

    // PAID -> 배송 준비중 ( 허브에서 승인 시 )
    public void prepareShipment() {
        validateCurrentStatus(OrderStatus.PAID);
        this.orderStatus = OrderStatus.PREPARING_SHIPMENT;
    }

    // 배송 준비중(PREPARING_SHIPMENT) -> 자정이 넘으면 IN_TRANSIT
    public void startDelivery(UUID deliveryId) {
        validateCurrentStatus(OrderStatus.PREPARING_SHIPMENT);
        this.deliveryId = deliveryId;
        this.orderStatus = OrderStatus.IN_TRANSIT;
    }

    // IN_TRANSIT -> 수령 업체로 배송이 완료되면 DELIVERED
    public void completeDelivery() {
        validateCurrentStatus(OrderStatus.IN_TRANSIT);
        this.orderStatus = OrderStatus.DELIVERED;
    }

    // 취소 요청
    public void requestCancel() {
        if (this.orderStatus == OrderStatus.DELIVERED) {
            throw new IllegalStateException("이미 배송 완료된 주문은 취소할 수 없습니다.");
        }
        this.orderStatus = OrderStatus.CANCEL_REQUESTED;
    }

    // 취소 완료
    public void cancel() {
        validateCurrentStatus(OrderStatus.CANCEL_REQUESTED);
        this.orderStatus = OrderStatus.CANCELLED;
    }

    // 환불 로직

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

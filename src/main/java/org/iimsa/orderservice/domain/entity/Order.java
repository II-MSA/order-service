package org.iimsa.orderservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "productName", length = 100)
    private String productName;

    @Column(name = "supplier_id")
    private UUID supplierId;

    @Column(name = "supplierName", length = 100)
    private String supplierName;

    @Column(name = "receiver_id")
    private UUID receiverId;

    @Column(name = "receiverName", length = 100)
    private String receiverName;

    @Column(name = "companyManagerId")
    private UUID companyManagerId;

    @Column(name = "companyManagerName", length = 100)
    private String companyManagerName;

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
    public Order(Integer quantity, UUID productId, String productName,
                 UUID supplierId, String supplierName, UUID receiverId,
                 String receiverName, UUID companyManagerId,
                 String companyManagerName, OrderStatus orderStatus,
                 String requestDetails) {
        this.quantity = quantity;
        this.productId = productId;
        this.productName = productName;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.companyManagerId = companyManagerId;
        this.companyManagerName = companyManagerName;
        this.orderStatus = orderStatus;
        this.requestDetails = requestDetails;
    }

    public static Order create(
            Integer quantity,
            UUID productId,
            String productName,
            UUID supplierId,
            String supplierName,
            UUID receiverId,
            String receiverName,
            UUID companyManagerId,
            String companyManagerName,
            String requestDetails,
            PaymentMethod paymentMethod
    ) {
        OrderStatus status = (paymentMethod == PaymentMethod.CARD)
                ? OrderStatus.PAID
                : OrderStatus.PENDING_PAYMENT;

        return Order.builder()
                .quantity(quantity)
                .productId(productId)
                .productName(productName)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .receiverId(receiverId)
                .receiverName(receiverName)
                .companyManagerId(companyManagerId)
                .companyManagerName(companyManagerName)
                .requestDetails(requestDetails)
                .orderStatus(status)
                .build();
    }

    // ===========================
    // 상태 변경 로직
    // ===========================

    // 무통장 입금 확인 후 PAID로 변경
    public void markAsPaid() {
        validateCurrentStatus(OrderStatus.PENDING_PAYMENT);
        this.orderStatus = OrderStatus.PAID;
    }

    // PAID
    public void prepareShipment() {
        validateCurrentStatus(OrderStatus.PAID);
        this.orderStatus = OrderStatus.PREPARING_SHIPMENT;
    }

    // 배송 시작
    public void startDelivery(UUID deliveryId) {
        validateCurrentStatus(OrderStatus.PREPARING_SHIPMENT);
        this.deliveryId = deliveryId;
        this.orderStatus = OrderStatus.IN_TRANSIT;
    }

    // 배송 완료
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

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
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.iimsa.common.domain.BaseEntity;

// protected는 같은 패키지만 적용돼서 엔티티랑 VO를 같은 패키지 아래에 둔다.
@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is NULL")
public class Order extends BaseEntity {

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
        // 주문 생성 시점에 배송 ID를 결정
        this.deliveryId = UUID.randomUUID();
    }

    public static Order create(
            Product product,
            Supplier supplier,
            Receiver receiver,
            String requestDetails
    ) {
        // 주문이 생성될 때, 주문 상태는 ORDER_CREATED로 생성되야 한다.
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
    // 주문 정보 수정시
    // ===========================

    // 발주를 넣을 상품에 대한 정보 수정시 ( 상품 ID, 상품명, 주문할 수량 )
    public void updateProduct(Product product) {
        validateCurrentStatus(OrderStatus.ORDER_CREATED);
        this.product = product;
    }

    // 수령 주소, 수령자 정보 수정시
    public void updateDeliveryInfo(Receiver receiver, String requestDetails) {
        validateCurrentStatus(OrderStatus.ORDER_CREATED); // 생성 상태일 때만 수정 가능
        this.receiver = receiver;
        this.requestDetails = requestDetails;
    }

    // 주문 접수 후, 공급 업체의 정보가 바뀔 때
    public void updateSupplier(Supplier supplier) {
        validateCurrentStatus(OrderStatus.ORDER_CREATED);
        this.supplier = supplier;
    }

    // ===========================
    // 주문 상태 변경 로직
    // ===========================

    // 주문 생성 -> 주문 확정 ( "자정 스케쥴러"에 의해 주문이 확정된 상태 )
    public void fixOrder() {
        validateCurrentStatus(OrderStatus.ORDER_CREATED);
        if (deliveryId == null) {
            throw new IllegalArgumentException("배송 ID가 할당되지 않은 주문은 확정할 수 없습니다.");
        }
        this.orderStatus = OrderStatus.ORDER_FIXED;
    }

    // 주문 생성 -> 주문 취소 ( 12시 전에만 가능 )
    // 예시: 호출하는 쪽(Service)에서 LocalDateTime.now()를 넘겨줌
    public void cancel(LocalDateTime now) {
        validateCurrentStatus(OrderStatus.ORDER_CREATED);
        if (now.getHour() >= 12) {
            throw new IllegalStateException("12시 이후에는 주문을 취소할 수 없습니다.");
        }
        this.orderStatus = OrderStatus.ORDER_CANCELLED;
    }

    // 주문 확정 -> 시스템 취소
    public void cancelBySystem() {
        validateCurrentStatus(OrderStatus.ORDER_FIXED);
        this.orderStatus = OrderStatus.SYSTEM_CANCELLED;
    }

    // 주문 삭제: 시스템에서 숨김 (Soft Delete)
    public void delete(String deletedBy) {
        if (deletedBy == null || deletedBy.isBlank()) {
            throw new IllegalArgumentException("deletedBy는 비어 있을 수 없습니다.");
        }
        if (this.deletedAt != null) {
            return; // idempotent soft-delete
        }
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
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

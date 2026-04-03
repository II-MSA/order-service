package org.iimsa.orderservice.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.orderservice.application.dto.command.CreateOrderCommand;
import org.iimsa.orderservice.application.dto.command.UpdateProductCommand;
import org.iimsa.orderservice.domain.model.Order;
import org.iimsa.orderservice.domain.model.OrderStatus;
import org.iimsa.orderservice.domain.model.Product;
import org.iimsa.orderservice.domain.model.Receiver;
import org.iimsa.orderservice.domain.model.Supplier;
import org.iimsa.orderservice.domain.repository.OrderRepository;
import org.iimsa.orderservice.domain.service.CompanyProvider;
import org.iimsa.orderservice.domain.service.ProductProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductProvider productProvider;
    private final CompanyProvider companyProvider;

    // 발주 이벤트를 Listen하고 있다가 주문 생성 이벤트 내부에서 사용할 메서드
    public UUID createOrder(CreateOrderCommand command) {
        // 1. 외부 서비스를 통해 실제 데이터(이름, 가격 등)를 채운 VO 생성
        Product product = Product.from(command.productId(), command.quantity(), productProvider);
        Supplier supplier = Supplier.from(command.supplierId(), companyProvider);
        Receiver receiver = Receiver.from(command.receiverId(), companyProvider);

        Order order = Order.create(
                product,
                supplier,
                receiver,
                command.requestDetails()
        );
        return orderRepository.save(order).getId();
    }

    // 이 부분은 HTTP API로 처리할 예정
//    // 이벤트 리스너, 수령 업체 서버로부터 전달받은 발주 이벤트를 처리합니다.
//    @KafkaListener(topics = "partner.order.placed", groupId = "order-service-group")
//    public void handlePartnerOrderEvent(PartnerOrderEvent payload) {
//        log.info("수령 업체 발주 이벤트 수신: {}", payload);
//
//        try {
//            // 제공해주신 CreateOrderCommand 구조에 맞게 매핑
//            CreateOrderCommand command = new CreateOrderCommand(
//                    payload.supplierId(),
//                    payload.receiverId(),
//                    payload.productId(),
//                    payload.quantity(),
//                    payload.memo()       // requestDetails에 매핑
//            );
//            // 동일한 서비스 메서드 호출
//            this.createOrder(command);
//
//        } catch (IllegalArgumentException e) {
//            log.error("잘못된 결제 방식 또는 데이터 형식입니다: {}", e.getMessage());
//        } catch (Exception e) {
//            log.error("이벤트를 통한 주문 생성 중 예기치 않은 오류 발생: {}", e.getMessage());
//            // 필요 시 에러 큐(DLQ) 전송 로직 추가
//        }
//    }

    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("해당하는 ID의 주문이 없습니다.")
        );
    }

    // ===========================
    // 주문 수정 로직 (Application Service)
    // 주문이 생성된 후, 주문서의 내용을 수정 하는 경우
    // ===========================

    /**
     * 1. 주문이 생성되고 나서, 상품이랑 수량을 수정하는 경우 ( 사실상 주문서가 새로 생성되는 느낌 )
     */
    public void updateOrderProduct(UUID orderId, UpdateProductCommand command) {
        Order order = getOrder(orderId);

        // 새로운 상품 VO 생성 (재고/이름 등 외부 검증 포함)
        Product newProduct = Product.from(command.productId(), command.quantity(), productProvider);

        // 도메인 모델에 변경 위임
        order.updateProduct(newProduct);

        log.info("주문 상품 수정 완료: orderId={}, productId={}", orderId, command.productId());
    }

    /**
     * [수령업체] 업체 정보 동기화 (이벤트 수신용) 업체 서버의 마스터 데이터가 변경되었을 때, 주문서의 정보를 최신화합니다.
     */
    public void syncReceiverInfo(UUID orderId, UUID receiverId) {
        Order order = getOrder(orderId);
        // CompanyProvider를 통해 업체 서버의 최신 name, address를 가져옴
        Receiver updatedReceiver = Receiver.from(receiverId, companyProvider);

        order.updateDeliveryInfo(updatedReceiver, order.getRequestDetails());
        log.info("수령업체 마스터 정보 동기화 완료: orderId={}", orderId);
    }

    /**
     * [공급업체] 업체 정보 동기화 (이벤트 수신용)
     */
    public void syncSupplierInfo(UUID orderId, UUID supplierId) {
        Order order = getOrder(orderId);
        // CompanyProvider를 통해 업체 서버의 최신 정보(출고지 등)를 가져옴
        Supplier updatedSupplier = Supplier.from(supplierId, companyProvider);

        order.updateSupplier(updatedSupplier);
        log.info("공급업체 마스터 정보 동기화 완료: orderId={}", orderId);
    }

    /**
     * 1. 주문 취소 (비즈니스적 처리) 주문 상태를 CANCELLED로 변경하며, 사용자가 여전히 내역을 볼 수 있습니다.
     */
    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = getOrder(orderId);
        // 엔티티 내부 로직: 12시 이전 여부 확인 및 상태 변경
        order.cancel(LocalDateTime.now());
        log.info("주문 취소 완료: orderId={}", orderId);
    }

    /**
     * 2. 주문 삭제 (시스템적 처리 - Soft Delete) DB의 deleted_at을 채워 @SQLRestriction에 의해 조회에서 제외됩니다.
     */
    @Transactional
    public void deleteOrder(UUID orderId, String deletedBy) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("삭제할 주문이 존재하지 않습니다.")
        );

        // 엔티티에 별도로 구현할 delete 메서드 호출
        // (Order 엔티티에 delete(String deletedBy) 메서드가 있다고 가정)
        order.delete(deletedBy);

        log.info("주문 삭제 완료(Soft Delete): orderId={}, deletedBy={}", orderId, deletedBy);
    }

    // 주문서를 허브 서버로 전달 ( 배송중으로 변경 )
    public void startBulkDelivery() {
        // 1. 배송 대기 중(ORDER_CREATED)인 주문들 조회
        List<Order> waitingOrders = orderRepository.findAllByOrderStatus(OrderStatus.ORDER_CREATED);

        // 2. 각 주문의 상태를 배송중(IN_TRANSIT)으로 변경
        for (Order order : waitingOrders) {
            try {
                order.startDelivery();
                // 여기에 허브 서버로 전송하는 로직(API 호출 or Message Queue) 추가 가능
                // 3. 허브 서버로 전송하는 로직 (이 시점에 deliveryId와 함께 전송)
                // hubEventProducer.send(order);
            } catch (Exception e) {
                // 특정 주문 실패 시 로그 남기고 다음 주문 진행
                log.error("주문 ID {} 배송 처리 실패", order.getId());
            }
        }
        // Transactional에 의해 변경사항이 DB에 자동 반영(Dirty Checking)
    }
}

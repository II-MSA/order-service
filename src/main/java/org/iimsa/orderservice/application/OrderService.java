package org.iimsa.orderservice.application;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.orderservice.application.dto.command.CreateOrderCommand;
import org.iimsa.orderservice.application.dto.command.UpdateProductCommand;
import org.iimsa.orderservice.domain.events.OrderEventProducer;
import org.iimsa.orderservice.domain.events.payload.OrderCreatedPayload;
import org.iimsa.orderservice.domain.model.Order;
import org.iimsa.orderservice.domain.model.OrderStatus;
import org.iimsa.orderservice.domain.model.Product;
import org.iimsa.orderservice.domain.model.Receiver;
import org.iimsa.orderservice.domain.model.Supplier;
import org.iimsa.orderservice.domain.repository.OrderRepository;
import org.iimsa.orderservice.domain.service.CompanyProvider;
import org.iimsa.orderservice.domain.service.ProductProvider;
import org.iimsa.orderservice.infrastructure.messaging.kafka.OrderTopicProperties;
import org.iimsa.orderservice.presentation.dto.response.ListOrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final OrderEventProducer orderEventProducer;
    private final OrderTopicProperties orderTopicProperties; // 1. 프로퍼티 주입

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

        if (command.productId() != null) { // 상품 자체가 바뀌는 경우
            // 새로운 상품 VO 생성 (재고/이름 등 외부 검증 포함)
            Product newProduct = Product.from(command.productId(), command.quantity(), productProvider);

            // 도메인 모델에 변경 위임
            order.updateProduct(newProduct);
        } else { // 수량만 바뀌는 경우 (기존 상품 ID 유지, 수량만 업데이트)
            // 엔티티에 수량만 변경하는 전용 메서드가 있다면 그것을 호출
            order.updateQuantity(command.quantity());
        }

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
    public void cancelOrder(UUID orderId) {
        Order order = getOrder(orderId);
        // 엔티티 내부 로직: 12시 이전 여부 확인 및 상태 변경
        order.cancel(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        log.info("주문 취소 완료: orderId={}", orderId);
    }

    /**
     * 2. 주문 삭제 (시스템적 처리 - Soft Delete) DB의 deleted_at을 채워 @SQLRestriction에 의해 조회에서 제외됩니다.
     */
    public void deleteOrder(UUID orderId, String deletedBy) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("삭제할 주문이 존재하지 않습니다.")
        );

        // 엔티티에 별도로 구현할 delete 메서드 호출
        order.delete(deletedBy);

        log.info("주문 삭제 완료(Soft Delete): orderId={}, deletedBy={}", orderId, deletedBy);
    }

    /**
     * 3. 시스템에 의한 주문 취소 (이벤트 수신용) 배송 서버(Delivery)나 허브 서버(Hub)에서 재고 부족, 배송 불가 등의 사유로 취소 요청이 올 때 사용합니다.
     */
    public void cancelOrderBySystem(UUID orderId) {
        Order order = getOrder(orderId);

        // 엔티티 내부 로직 호출: 상태를 CANCELLED 등으로 변경
        order.cancelBySystem();

        log.info("시스템에 의한 주문 취소 완료: orderId={}", orderId);
    }

    // 자정이 되어 주문서를 확정한 후, 허브 서버로 전달한다.
    public void fixOrderAndPublishEventToHub(String correlationId) {
        // 1. 배송 대기 중(ORDER_CREATED)인 주문들 조회
        List<Order> waitingOrders = orderRepository.findAllByOrderStatus(OrderStatus.ORDER_CREATED);

        // 2. 각 주문의 상태를 주문 확정(ORDER_FIXED)으로 변경
        for (Order order : waitingOrders) {
            try {
                order.fixOrder();

                OrderCreatedPayload payload = new OrderCreatedPayload(
                        correlationId,                                   // String correlationId
                        order.getId(),                                    // UUID orderId
                        order.getProduct().getProductId(),                // UUID productId
                        order.getProduct().getQuantity(),
                        // Integer productQuantity (order에서 가져온다고 가정)
                        order.getProduct().getProductName(),              // String productName
                        order.getReceiver().getReceiverId(),              // UUID receiverId
                        order.getReceiver().getReceiverName(),            // String receiverName
                        order.getReceiver().getReceiverHubId(),           // UUID receiverHubId
                        order.getReceiver().getReceiverHubName(),         // String receiverHubName
                        order.getSupplier().getSupplierId(),              // UUID supplierId
                        order.getSupplier().getSupplierName(),            // String supplierName
                        order.getSupplier().getSupplierHubId(),           // UUID supplierHubId
                        order.getSupplier().getSupplierHubName(),         // String supplierHubName
                        order.getDeliveryId(),                            // UUID deliveryId
                        order.getRequestDetails()                         // String requestDetails
                );

                // 3. 인프라 계층의 Producer 호출 (Events.trigger 실행)
                // 인프라 스타일을 따라 properties에서 토픽명을 꺼내 전달
                orderEventProducer.orderCreatedEvent(
                        correlationId,
                        "ORDER",
                        order.getId().toString(),
                        orderTopicProperties.created(), // 설정 파일(yml)에 정의된 토픽명
                        payload
                );
                log.info("주문 확정 및 이벤트 발행 완료: orderId={}, Topic={}",
                        order.getId(), orderTopicProperties.created());
            } catch (Exception e) {
                log.error("주문 ID {} 배송 처리 실패: {}", order.getId(), e.getMessage(), e);
            }
        }
    }

    public Page<ListOrderResponseDto> searchOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return orderPage.map(order -> new ListOrderResponseDto(
                order.getId(),                               // orderId
                order.getProduct().getProductId(),           // productId
                order.getProduct().getQuantity(),            // productQuantity
                order.getProduct().getProductName(),         // productName
                order.getReceiver().getReceiverId(),         // receiverId
                order.getReceiver().getReceiverName(),       // receiverName
                order.getReceiver().getReceiverHubId(),      // receiverHubId
                order.getReceiver().getReceiverHubName(),    // receiverHubName
                order.getSupplier().getSupplierId(),         // supplierId
                order.getSupplier().getSupplierName(),       // supplierName
                order.getSupplier().getSupplierHubId(),      // supplierHubId
                order.getSupplier().getSupplierHubName(),    // supplierHubName
                order.getDeliveryId(),                       // deliveryId
                order.getRequestDetails()                    // requestDetails
        ));
    }
}

package org.iimsa.orderservice.application;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iimsa.orderservice.application.dto.command.CreateOrderCommand;
import org.iimsa.orderservice.application.dto.result.PartnerOrderEvent;
import org.iimsa.orderservice.domain.model.Order;
import org.iimsa.orderservice.domain.model.Product;
import org.iimsa.orderservice.domain.model.Receiver;
import org.iimsa.orderservice.domain.model.Supplier;
import org.iimsa.orderservice.domain.repository.OrderRepository;
import org.iimsa.orderservice.domain.service.CompanyProvider;
import org.iimsa.orderservice.domain.service.ProductProvider;
import org.springframework.kafka.annotation.KafkaListener;
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

    // 이벤트 리스너, 수령 업체 서버로부터 전달받은 발주 이벤트를 처리합니다.
    @KafkaListener(topics = "partner.order.placed", groupId = "order-service-group")
    public void handlePartnerOrderEvent(PartnerOrderEvent payload) {
        log.info("수령 업체 발주 이벤트 수신: {}", payload);

        try {
            // 제공해주신 CreateOrderCommand 구조에 맞게 매핑
            CreateOrderCommand command = new CreateOrderCommand(
                    payload.supplierId(),
                    payload.receiverId(),
                    payload.productId(),
                    payload.quantity(),
                    payload.memo()       // requestDetails에 매핑
            );
            // 동일한 서비스 메서드 호출
            this.createOrder(command);

        } catch (IllegalArgumentException e) {
            log.error("잘못된 결제 방식 또는 데이터 형식입니다: {}", e.getMessage());
        } catch (Exception e) {
            log.error("이벤트를 통한 주문 생성 중 예기치 않은 오류 발생: {}", e.getMessage());
            // 필요 시 에러 큐(DLQ) 전송 로직 추가
        }
    }
}

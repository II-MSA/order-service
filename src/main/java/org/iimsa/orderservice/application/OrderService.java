package org.iimsa.orderservice.application;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.application.dto.command.CreateOrderCommand;
import org.iimsa.orderservice.domain.model.Order;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductProvider productProvider;
    private final CompanyProvider companyProvider;

    public UUID createOrder(CreateOrderCommand command) {
        // 1. 외부 서비스를 통해 실제 데이터(이름, 가격 등)를 채운 VO 생성
        Product product = Product.from(command.productId(), command.quantity(), productProvider);
        Supplier supplier = Supplier.from(command.supplierId(), companyProvider);
        Receiver receiver = Receiver.from(command.receiverId(), companyProvider);


        Order order = Order.create(product,
                supplier,
                receiver,
                command.requestDetails(),
                command.paymentMethod()
        );
        return orderRepository.save(order).getId();
    }

//    public UUID createOrder(CreateOrderCommand command) {
//        Order order = command.toEntity(); // Command를 엔티티로 변환
//
//        Order savedOrder = orderRepository.save(order);
//        return savedOrder.getId(); // 생성된 식별자만 반환
//    }
}


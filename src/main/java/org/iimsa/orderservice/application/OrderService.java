package org.iimsa.orderservice.application;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.application.dto.command.CreateOrderCommand;
import org.iimsa.orderservice.domain.entity.Order;
import org.iimsa.orderservice.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public UUID createOrder(CreateOrderCommand command) {
        Order order = command.toEntity(); // Command를 엔티티로 변환

        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId(); // 생성된 식별자만 반환
    }
}


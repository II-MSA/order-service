package org.iimsa.orderservice.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.domain.entity.Order;
import org.iimsa.orderservice.domain.repository.OrderRepository;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
    @Override
    public Order save(Order order) {
        return jpaOrderRepository.save(order);
    }
}

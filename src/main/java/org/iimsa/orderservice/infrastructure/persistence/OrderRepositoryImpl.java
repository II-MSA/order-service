package org.iimsa.orderservice.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.domain.model.Order;
import org.iimsa.orderservice.domain.model.OrderStatus;
import org.iimsa.orderservice.domain.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Order save(Order order) {
        return jpaOrderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaOrderRepository.findById(orderId);
    }

    @Override
    public List<Order> findAllByOrderStatus(OrderStatus orderStatus) {
        return jpaOrderRepository.findByOrderStatus(orderStatus);
    }

    // 구현만 해두고, 사용은 아직 안함
    @Override
    @Transactional // 수정 작업이므로 반드시 트랜잭션 필요
    public int updateStatusBulk(OrderStatus from, OrderStatus to) {
        return jpaOrderRepository.bulkUpdateStatus(from, to);
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return jpaOrderRepository.findAll(pageable);
    }
}

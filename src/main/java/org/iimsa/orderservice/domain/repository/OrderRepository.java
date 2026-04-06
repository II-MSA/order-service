package org.iimsa.orderservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.iimsa.orderservice.domain.model.Order;
import org.iimsa.orderservice.domain.model.OrderStatus;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    List<Order> findAllByOrderStatus(OrderStatus orderStatus);

    // 구현은 하지만, 사용은 안함.
    int updateStatusBulk(OrderStatus from, OrderStatus to);
}

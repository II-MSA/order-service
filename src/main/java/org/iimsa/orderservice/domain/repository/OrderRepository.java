package org.iimsa.orderservice.domain.repository;

import org.iimsa.orderservice.domain.model.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {

    Order save(Order order);
}

package org.iimsa.orderservice.infrastructure.persistence;

import java.util.UUID;
import org.iimsa.orderservice.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<Order, UUID> {
}

package org.iimsa.orderservice.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.iimsa.orderservice.domain.model.Order;
import org.iimsa.orderservice.domain.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaOrderRepository extends JpaRepository<Order, UUID> {

    // 구현만 해두고, 사용은 아직 안함
    @Modifying(clearAutomatically = true) // 중요: 실행 후 영속성 컨텍스트를 비워줌
    @Query("UPDATE Order o SET o.orderStatus = :toStatus WHERE o.orderStatus = :fromStatus")
    int bulkUpdateStatus(
            @Param("fromStatus") OrderStatus fromStatus,
            @Param("toStatus") OrderStatus toStatus
    );

    List<Order> findByOrderStatus(OrderStatus orderStatus);
}

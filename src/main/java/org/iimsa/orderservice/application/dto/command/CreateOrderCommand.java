package org.iimsa.orderservice.application.dto.command;

import org.iimsa.orderservice.domain.entity.Order;
import org.iimsa.orderservice.presentation.dto.CreateOrderRequestDto;

public class CreateOrderCommand {

    public static CreateOrderCommand from(CreateOrderRequestDto request) {
        return new  CreateOrderCommand();
    }

    // Command에서 Entity로 변환하는 책임
    public Order toEntity() {
        return Order.builder()
                .build();
    }
}

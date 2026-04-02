package org.iimsa.orderservice.presentation.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.application.OrderService;
import org.iimsa.orderservice.application.dto.command.CreateOrderCommand;
import org.iimsa.orderservice.presentation.dto.CreateOrderRequestDto;
import org.iimsa.orderservice.presentation.dto.CreateOrderResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto request) {
        CreateOrderCommand command = CreateOrderCommand.from(request);

        UUID orderId = orderService.createOrder(command);

        return new CreateOrderResponseDto(orderId);
    }


}

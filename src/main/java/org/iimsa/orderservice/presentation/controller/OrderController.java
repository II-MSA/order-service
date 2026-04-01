package org.iimsa.orderservice.presentation.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.application.OrderService;
import org.iimsa.orderservice.application.dto.command.CreateOrderCommand;
import org.iimsa.orderservice.presentation.dto.CreateOrderRequestDto;
import org.iimsa.orderservice.presentation.dto.CreateOrderResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CreateOrderResponseDto> createOrder(CreateOrderRequestDto request) {
        CreateOrderCommand command = CreateOrderCommand.from(request);

        UUID orderId = orderService.createOrder(command);

        CreateOrderResponseDto response = new CreateOrderResponseDto(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

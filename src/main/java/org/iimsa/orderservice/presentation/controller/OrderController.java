package org.iimsa.orderservice.presentation.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.application.OrderService;
import org.iimsa.orderservice.application.dto.command.CreateOrderCommand;
import org.iimsa.orderservice.application.dto.command.UpdateProductCommand;
import org.iimsa.orderservice.domain.model.Order;
import org.iimsa.orderservice.presentation.dto.request.CreateOrderRequestDto;
import org.iimsa.orderservice.presentation.dto.request.UpdateRequestDto;
import org.iimsa.orderservice.presentation.dto.response.CreateOrderResponseDto;
import org.iimsa.orderservice.presentation.dto.response.FindOrderResponseDto;
import org.iimsa.orderservice.presentation.dto.response.UpdateResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto) {
        CreateOrderCommand command = CreateOrderCommand.from(requestDto);

        UUID orderId = orderService.createOrder(command);

        return new CreateOrderResponseDto(orderId);
    }

    @GetMapping("{id}")
    public FindOrderResponseDto getOrder(@PathVariable("id") UUID orderId) {
        Order order = orderService.getOrder(orderId);
        return FindOrderResponseDto.from(order);
    }

    /**
     * 주문 정보 수정 API 전달되는 ID 값에 따라 상품 수정, 수령업체 동기화, 공급업체 동기화를 수행합니다.
     */
    @PatchMapping("/{id}")
    public UpdateResponseDto updateOrder(
            @PathVariable("id") UUID orderId,
            @RequestBody UpdateRequestDto requestDto) {

        StringBuilder message = new StringBuilder();

        // 1. 상품 및 수량 수정이 들어온 경우
        if (requestDto.productId() != null && requestDto.quantity() != null) {
            UpdateProductCommand command = new UpdateProductCommand(
                    requestDto.productId(),
                    requestDto.quantity()
            );
            orderService.updateOrderProduct(orderId, command);
            message.append("상품 정보 수정 완료. ");
        }

        // 2. 수령 업체 정보 동기화가 요청된 경우
        if (requestDto.receiverId() != null) {
            orderService.syncReceiverInfo(orderId, requestDto.receiverId());
            message.append("수령 업체 정보 동기화 완료. ");
        }

        // 3. 공급 업체 정보 동기화가 요청된 경우
        if (requestDto.supplierId() != null) {
            orderService.syncSupplierInfo(orderId, requestDto.supplierId());
            message.append("공급 업체 정보 동기화 완료. ");
        }

        if (message.isEmpty()) {
            return UpdateResponseDto.success(orderId, "수정할 정보가 전달되지 않았습니다.");
        }

        return UpdateResponseDto.success(orderId, message.toString().trim());
    }

    /**
     * 1. 주문 취소 (상태 변경) 사용자가 "취소 버튼"을 눌렀을 때 호출
     */
    @PatchMapping("/{id}/cancel")
    public UpdateResponseDto cancelOrder(@PathVariable("id") UUID orderId) {
        orderService.cancelOrder(orderId);
        return UpdateResponseDto.success(orderId, "주문이 정상적으로 취소되었습니다.");
    }

    /**
     * 2. 주문 삭제 (Soft Delete) 목록에서 완전히 지우고 싶거나, 관리자가 데이터를 무효화할 때 호출
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("id") UUID orderId, String userId) { // 임시로 유저 ID를 입력받음, 이후 실제 유저정보를 받아오도록 변경
        orderService.deleteOrder(orderId, userId);
    }
}

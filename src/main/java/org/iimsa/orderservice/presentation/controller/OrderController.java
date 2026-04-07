package org.iimsa.orderservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.iimsa.orderservice.presentation.dto.response.ListOrderResponseDto;
import org.iimsa.orderservice.presentation.dto.response.UpdateResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order API", description = "주문 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "새로운 주문을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponseDto createOrder(@RequestBody CreateOrderRequestDto requestDto) {
        CreateOrderCommand command = new CreateOrderCommand(
                requestDto.supplierId(),
                requestDto.receiverId(),
                requestDto.productId(),
                requestDto.quantity(),
                requestDto.requestDetails()
        );

        UUID orderId = orderService.createOrder(command);

        return new CreateOrderResponseDto(orderId);
    }

    @Operation(summary = "주문 단건 조회", description = "주문 ID를 통해 상세 정보를 조회합니다.")
    @GetMapping("{id}")
    public FindOrderResponseDto getOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable("id") UUID orderId) {
        Order order = orderService.getOrder(orderId);
        return FindOrderResponseDto.from(order);
    }

    @Operation(summary = "주문 정보 수정", description = "상품 변경, 수량 변경, 업체 정보 동기화를 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public UpdateResponseDto updateOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable("id") UUID orderId,
            @RequestBody UpdateRequestDto requestDto) {

        StringBuilder message = new StringBuilder();

        // 1. 상품 변경 시도 시 검증 (상품 ID는 있는데 수량이 없는 경우 차단)
        if (requestDto.productId() != null && requestDto.quantity() == null) {
            throw new IllegalArgumentException("상품 변경 시에는 수량 정보도 반드시 포함되어야 합니다.");
        }

        // 2. 서비스 호출 (수량만 있거나, 둘 다 있거나)
        if (requestDto.quantity() != null) {
            UpdateProductCommand command = new UpdateProductCommand(
                    requestDto.productId(), // null일 수 있음 (수량만 변경하는 경우)
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

    @Operation(summary = "주문 취소", description = "주문의 상태를 '취소'로 변경합니다.")
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public UpdateResponseDto cancelOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable("id") UUID orderId) {
        orderService.cancelOrder(orderId);
        return UpdateResponseDto.success(orderId, "주문이 정상적으로 취소되었습니다.");
    }

    @Operation(summary = "주문 삭제", description = "주문 데이터를 논리 삭제(Soft Delete) 처리합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public void deleteOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable("id") UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        orderService.deleteOrder(orderId, userDetails.getUsername());
    }

    @GetMapping("/search")
    @Operation(summary = "주문 목록 검색/페이징", description = "페이징 처리된 주문 목록을 조회합니다.")
    public Page<ListOrderResponseDto> search(
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return orderService.searchOrders(pageable);
    }
}

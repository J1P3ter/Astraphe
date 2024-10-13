package com.j1p3ter.orderserver.controller;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.orderserver.application.OrderService;
import com.j1p3ter.orderserver.application.dto.OrderRequestDto;
import com.j1p3ter.orderserver.application.dto.OrderResponseDto;
import com.j1p3ter.orderserver.domain.order.OrderState;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor  // final 필드를 자동으로 주입해주는 Lombok 어노테이션
public class OrderController {

    private final OrderService orderService;

    // 주문 생성 (헤더에서 X-USER-ID를 받아오고, 요청 본문에서 주문 정보를 처리)
    @Operation(summary = "Create Order")
    @PostMapping
    public ApiResponse<?> createOrder(
            @RequestHeader(name = "X-USER-ID") Long userId,  // 헤더에서 사용자 ID 받아옴
            @RequestBody OrderRequestDto orderRequestDto     // 요청 본문에서 주문 정보 받음
    ) {
        OrderResponseDto createdOrder = orderService.createOrder(userId, orderRequestDto);
        return ApiResponse.success(createdOrder);  // 성공적인 응답 반환
    }

    // 주문 상세 조회
    @Operation(summary = "Get order Info")
    @GetMapping("/{orderId}")
    public ApiResponse<?> getOrderInfo(
            @RequestHeader(name = "X-USER-ID") Long userId, // 헤더에서 사용자 ID 받아옴
            @PathVariable Long orderId                      // 경로 변수로 주문 ID 받음
    ){
        return ApiResponse.success(orderService.getOrder(orderId));
    }

    // 주문 조회
    @Operation(summary = "Get order List")
    @GetMapping
    public ApiResponse<?> searchOrder(
            @RequestHeader(name = "X-USER-ID", required = false) Long userId,
            @RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(defaultValue = "createdAt", name = "sort", required = false) String sort,
            @RequestParam(defaultValue = "DESC", name = "direction") String direction,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "state", required = false) OrderState state
    ){
        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.fromString(direction), sort));
        return ApiResponse.success(orderService.searchOrder(productName, state, pageable));
    }

    // 주문 상태 업데이트
    @Operation(summary = "Update Order Status")
    @PutMapping("/{orderId}")
    public ApiResponse<?> updateOrderStatus(
            @RequestHeader(name = "X-USER-ID") Long userId,    // 헤더에서 사용자 ID 받아옴
            @PathVariable Long orderId,                        // 경로 변수로 주문 ID 받음
            @RequestParam OrderState newStatus                     // 쿼리 파라미터로 새 상태 받음
    ) {
        OrderResponseDto updatedOrder = orderService.updateOrderStatus(orderId, newStatus, userId);
        return ApiResponse.success(updatedOrder);  // 성공적인 응답 반환
    }

    // 주문 삭제 논리적 삭제
    @Operation(summary = "Delete Order")
    @PutMapping("/delete/{orderId}")
    public ApiResponse<?> deleteOrder(
            @RequestHeader(name = "X-USER-ID") Long userId,  // 헤더에서 사용자 ID 받아옴
            @PathVariable Long orderId                       // 경로 변수로 주문 ID 받음
    ) {
        OrderResponseDto deletedOrder = orderService.deleteOrder(orderId, userId);
        return ApiResponse.success(deletedOrder);  // 성공적인 삭제 응답 반환
    }
}

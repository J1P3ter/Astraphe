package com.j1p3ter.orderserver.controller;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.orderserver.application.dto.payment.PaymentCreateResponseDto;
import com.j1p3ter.orderserver.application.service.TossPaymentService;
import com.j1p3ter.orderserver.application.service.request.TossPaymentCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor  // final 필드를 자동으로 주입해주는 Lombok 어노테이션
public class PaymentController {
    private final TossPaymentService tossPaymentService;

    @PostMapping("/toss")
    public ApiResponse<?> requestTossPayment(
            @Validated @RequestBody TossPaymentCreateRequestDto request
    ) {
        PaymentCreateResponseDto createdOrder = tossPaymentService.requestTossPayment(request);

        return ApiResponse.success(createdOrder);  // 성공적인 응답 반환
    }

}

package com.j1p3ter.orderserver.implementation;

import com.j1p3ter.orderserver.domain.payment.Payment;
import com.j1p3ter.orderserver.domain.payment.PaymentRepository;
import com.j1p3ter.orderserver.domain.payment.PaymentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentSaver {
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createTossPayment(Long orderId, String paymentKey, Integer amount) {
        Payment payment = Payment.of(
                orderId,
                paymentKey,
                amount,
                PaymentType.TOSS_PAYMENTS);

        return paymentRepository.save(payment);
    }
}

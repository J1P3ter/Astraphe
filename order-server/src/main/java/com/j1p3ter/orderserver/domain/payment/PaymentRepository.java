package com.j1p3ter.orderserver.domain.payment;

import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {

    Payment save(Payment payment);
}

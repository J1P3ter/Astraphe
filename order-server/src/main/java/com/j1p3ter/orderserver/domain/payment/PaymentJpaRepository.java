package com.j1p3ter.orderserver.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, String>, PaymentRepository {

}

package com.j1p3ter.orderserver.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query ("select od from OrderDetail od where od.order.orderId=:orderId")
    Optional<OrderDetail> findByOrderId(@Param("orderId") Long orderId);
}

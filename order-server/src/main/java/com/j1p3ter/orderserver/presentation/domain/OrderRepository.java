package com.j1p3ter.orderserver.presentation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> searchOrderByOrderNameContaining(String orderName, Pageable pageable);
}

package com.j1p3ter.orderserver.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndUserIdAndIsDeletedFalse(Long orderId, Long userId);

    Page<Order> findAllByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    Page<Order> findAllByIsDeletedFalse(Pageable pageable);

    // keyword가 null일 때 전체 검색, 그렇지 않을 때 keyword에 따라 검색
    @Query("SELECT o FROM Order o " +
            "JOIN o.orderDetails od " +  // Order와 OrderDetails를 JOIN
            "WHERE o.isDeleted = false " +
            "AND (:productName IS NULL OR od.productName LIKE %:productName%) " +
            "AND (:state IS NULL OR o.state = :state)")
    Page<Order> findAllByIsDeletedFalseAndKeyword(
            @Param("productName") String productName,
            @Param("state") OrderState state,
            Pageable pageable
    );

    // 사용자 ID와 주문 상태(OrderState)로 주문을 페이징하여 조회 (논리적 삭제된 주문은 제외)
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.state = :state AND o.isDeleted = false")
    Page<Order> findByUserIdAndOrderStatus(@Param("userId") Long userId, @Param("state") OrderState state, Pageable pageable);
}

package com.j1p3ter.orderserver.presentation.domain;

import com.j1p3ter.common.auditing.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@SQLRestriction("is_delete = false") // SQL 필터를 통해 삭제되지 않은 데이터만 조회
@Table(name = "tb_orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성되는 PK
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "delivery_price", nullable = false)
    private Integer deliveryPrice;

    private LocalDateTime deliveryDate;

    private String cancelReason;

    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private OrderState state;

    // User ID를 직접 참조
    @Column(name = "user_id", nullable = false)
    private Long userId; // User 서비스에서 가져온 사용자 ID

    // Address ID를 직접 참조
    @Column(name = "address_id", nullable = false)
    private Long addressId; // Address 서비스에서 가져온 주소 ID

}
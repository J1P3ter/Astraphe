package com.j1p3ter.orderserver.domain.order;

import com.j1p3ter.common.auditing.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@SQLRestriction("is_deleted = false") // SQL 필터를 통해 삭제되지 않은 데이터만 조회
@Table(name = "tb_orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성되는 PK
    @Column(name = "order_id")
    private Long orderId;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

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

    public void update(OrderState state) {
        this.state = state;
    }

    public void addOrderProduct(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }
}
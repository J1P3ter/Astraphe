package com.j1p3ter.orderserver.domain.order;

import com.j1p3ter.common.auditing.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@SQLRestriction("is_delete = false") // SQL 필터를 통해 삭제되지 않은 데이터만 조회
@Table(name = "tb_order_details")
public class OrderDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 주문 상세 아이디의 자동 생성
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    @Column(name = "product_id", nullable = false)
    private Integer productId; // 상품 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // 주문 아이디 (FK)

    private Integer quantity; // 주문 수량

    private Integer originalPrice; // 상품 원래 가격

    private Integer discountedPrice; // 상품 할인 가격

    private String productName; // 상품명

    @Column(name = "product_option", length = 100)
    private String productOption; // 상품 옵션

    private Short categoryCode; // 카테고리 코드

    private Long companyId; // 업체 아이디
}

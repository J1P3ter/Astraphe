package com.j1p3ter.orderserver.domain.payment;

import com.j1p3ter.common.auditing.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tb_payments")
@SQLRestriction("is_deleted = false") // SQL 필터를 통해 삭제되지 않은 데이터만 조회
@Builder // 빌더 패턴은 테스트 용도로만 사용
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 객체는 기본 생성자를 필요로 함
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성되는 PK
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String transactionId; // 각 결제 수단 별 고유한 결제 번호

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentMethod;

    private LocalDateTime approvedAt;

    private LocalDateTime settledAt;

    public static Payment of(
            Long orderId,
            String transactionId,
            Integer amount,
            PaymentType type
    ) {
        return Payment.builder()
                .orderId(orderId)
                .transactionId(transactionId)
                .amount(amount)
                .paymentMethod(type)
                .status(PaymentStatus.PENDING)
                .build();
    }

    /**
     * 결제 승인
     */
    public void approve() {
        this.status = PaymentStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * 결제 실패
     */
    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

}

package com.j1p3ter.productserver.domain.product;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "tb_product_options")
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "option_name", nullable = false, length = 20)
    private String optionName;

    @Column(name = "option_value", nullable = false, length = 100)
    private String optionValue;

    @Column(name = "option_price", nullable = false)
    private Integer optionPrice;
}

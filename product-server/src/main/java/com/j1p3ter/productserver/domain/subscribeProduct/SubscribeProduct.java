package com.j1p3ter.productserver.domain.subscribeProduct;

import com.j1p3ter.productserver.domain.product.Product;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "tb_subscirbe_product")
public class SubscribeProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscirbe_product_id")
    private Long id;

    @ManyToOne
    private Product product;

    private Long userId;

    public SubscribeProduct(Long userId, Product product){
        this.userId = userId;
        this.product = product;
    }
}

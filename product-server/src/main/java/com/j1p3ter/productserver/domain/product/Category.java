package com.j1p3ter.productserver.domain.product;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "tb_categories")
public class Category {

    @Id
    @Column(name = "category_code")
    private Long categoryCode;

    @Column(name = "category_name", nullable = false, length = 20)
    private String categoryName;
}

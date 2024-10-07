package com.j1p3ter.productserver.domain.product;

import com.j1p3ter.common.auditing.BaseEntity;
import com.j1p3ter.productserver.domain.company.Company;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@SQLRestriction("is_deleted is false")
@Table(name = "tb_products")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "product_name", nullable = false, length = 50)
    private String productName;

    @Column(name = "product_image_url")
    private String productImgUrl;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "description_image_url")
    private String descriptionImgUrl;

    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "discounted_price", nullable = false)
    private Integer discountedPrice;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> productOptions;

    @ManyToOne
    @JoinColumn(name = "category_code", nullable = false)
    private Category category;

    @Column(name = "sale_start_time", nullable = false)
    private LocalDateTime saleStartTime;

    @Column(name = "sale_end_time", nullable = false)
    private LocalDateTime saleEndTime;

    @Column(name = "rate", nullable = false)
    private Double rate;

    @Column(name = "is_soldout", nullable = false)
    private Boolean isSoldout;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden;

    public void addProductOption(ProductOption productOption){
        this.productOptions.add(productOption);
    }
}

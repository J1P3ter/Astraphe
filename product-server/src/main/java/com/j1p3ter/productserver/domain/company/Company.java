package com.j1p3ter.productserver.domain.company;

import com.j1p3ter.common.auditing.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "tb_companies")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "company_name", nullable = false, length = 20)
    private String companyName;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

}
package com.j1p3ter.productserver.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {
    Page<Product> searchByCompanyName(String companyName, Long categoryCode, Pageable pageable);
    Page<Product> searchByProductName(String productName, Long categoryCode, Pageable pageable);
}

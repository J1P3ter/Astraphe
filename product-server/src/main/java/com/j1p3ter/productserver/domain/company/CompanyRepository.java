package com.j1p3ter.productserver.domain.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Page<Company> searchCompanyByCompanyNameContaining(String companyName, Pageable pageable);
}

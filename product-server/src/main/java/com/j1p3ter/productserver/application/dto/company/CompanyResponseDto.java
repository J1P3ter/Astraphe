package com.j1p3ter.productserver.application.dto.company;

import com.j1p3ter.productserver.domain.company.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDto {

    private Long id;
    private Long userId;
    private String companyName;
    private String description;
    private String address;

    public static CompanyResponseDto fromCompany(Company company) {
        return CompanyResponseDto.builder()
                .id(company.getId())
                .userId(company.getUserId())
                .companyName(company.getCompanyName())
                .description(company.getDescription())
                .address(company.getAddress())
                .build();
    }
}

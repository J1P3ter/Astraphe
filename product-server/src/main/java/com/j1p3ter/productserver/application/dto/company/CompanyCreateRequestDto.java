package com.j1p3ter.productserver.application.dto.company;

import com.j1p3ter.productserver.domain.company.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreateRequestDto {

    private Long userId;
    private String companyName;
    private String description;
    private String address;

    public Company toEntity(){
        return Company.builder()
                .userId(this.userId)
                .companyName(this.companyName)
                .description(this.description)
                .address(this.address)
                .build();
    }
}

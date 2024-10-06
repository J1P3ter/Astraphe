package com.j1p3ter.productserver.application.dto.company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateRequestDto {
    private String companyName;
    private String description;
    private String address;
}

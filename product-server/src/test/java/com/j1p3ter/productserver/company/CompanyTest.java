package com.j1p3ter.productserver.company;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.productserver.application.CompanyService;
import com.j1p3ter.productserver.application.dto.company.CompanyCreateRequestDto;
import com.j1p3ter.productserver.application.dto.company.CompanyResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class CompanyTest {

    @Autowired
    CompanyService companyService;

    @Test
    @DisplayName("Create Company Test")
    @Transactional
    void createCompanyTest() {
        //Given
        CompanyCreateRequestDto companyCreateRequestDto = new CompanyCreateRequestDto(
                1L,
                "Company1",
                "Description1",
                "Address1"
        );

        //When
        CompanyResponseDto createdCompany = companyService.createCompany(companyCreateRequestDto);

        //Then
        assertThat(createdCompany).isNotNull();
        assertThat(createdCompany.getUserId()).isEqualTo(1L);
        assertThat(createdCompany.getCompanyName()).isEqualTo("Company1");
        assertThat(createdCompany.getDescription()).isEqualTo("Description1");
        assertThat(createdCompany.getAddress()).isEqualTo("Address1");
    }

    @Test
    @DisplayName("Create Company Fail Test")
    @Transactional
    void createCompanyFailTest() {
        // Company Name이 20자를 넘는 경우 Fail
        //Given
        CompanyCreateRequestDto companyCreateRequestDto = new CompanyCreateRequestDto(
                1L,
                "Company1 Failllllllllllllllllllllllllll",
                "Description1",
                "Address1"
        );

        //When - Then
        assertThatThrownBy(() -> companyService.createCompany(companyCreateRequestDto)).isInstanceOf(ApiException.class);
    }
}

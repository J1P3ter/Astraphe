package com.j1p3ter.productserver.company;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.productserver.application.CompanyService;
import com.j1p3ter.productserver.application.dto.company.CompanyCreateRequestDto;
import com.j1p3ter.productserver.application.dto.company.CompanyResponseDto;
import com.j1p3ter.productserver.application.dto.company.CompanyUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CompanyTest {

    @Autowired
    CompanyService companyService;

    CompanyResponseDto createdCompany;

    @BeforeEach
    void setUp() {
        CompanyCreateRequestDto companyCreateRequestDto = new CompanyCreateRequestDto(
                1L,
                "CompanyNameforTest",
                "Description1",
                "Address1"
        );

        createdCompany = companyService.createCompany(companyCreateRequestDto);
    }

    @Test
    @DisplayName("Create Company Test")
    void createCompanyTest() {
        //Given - When Setup 시 추가

        //Then
        assertThat(createdCompany).isNotNull();
        assertThat(createdCompany.getUserId()).isEqualTo(1L);
        assertThat(createdCompany.getCompanyName()).isEqualTo("CompanyNameforTest");
        assertThat(createdCompany.getDescription()).isEqualTo("Description1");
        assertThat(createdCompany.getAddress()).isEqualTo("Address1");
    }

    @Test
    @DisplayName("Create Company Fail Test")
    void createCompanyFailTest() {
        // Company Name이 20자를 넘는 경우 Fail
        //Given
        CompanyCreateRequestDto companyCreateRequestDto = new CompanyCreateRequestDto(
                1L,
                "CompanyNameforTest Failllllllllllllllllllllllllll",
                "Description1",
                "Address1"
        );

        //When - Then
        assertThatThrownBy(() -> companyService.createCompany(companyCreateRequestDto)).isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Get Company Info Test")
    void getCompanyInfoTest() {
        // Given-When
        CompanyResponseDto companyResponseDto = companyService.getCompany(createdCompany.getId());

        // Then
        assertThat(companyResponseDto).isNotNull();
        assertThat(companyResponseDto.getUserId()).isEqualTo(createdCompany.getUserId());
        assertThat(companyResponseDto.getCompanyName()).isEqualTo(createdCompany.getCompanyName());
        assertThat(companyResponseDto.getDescription()).isEqualTo(createdCompany.getDescription());
        assertThat(companyResponseDto.getAddress()).isEqualTo(createdCompany.getAddress());

    }

    @Test
    @DisplayName("Search Company Test")
    void searchCompanyTest() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.fromString("DESC"), "createdAt"));

        // When
        Page<CompanyResponseDto> companyResponseDtoPage = companyService.searchCompany("CompanyNameforTest", pageable);

        // Then
        assertThat(companyResponseDtoPage).isNotNull();
        assertThat(companyResponseDtoPage.getContent().size()).isEqualTo(1);
        assertThat(companyResponseDtoPage.getContent().get(0).getUserId()).isEqualTo(1L);
        assertThat(companyResponseDtoPage.getContent().get(0).getCompanyName()).isEqualTo("CompanyNameforTest");
        assertThat(companyResponseDtoPage.getContent().get(0).getDescription()).isEqualTo("Description1");
        assertThat(companyResponseDtoPage.getContent().get(0).getAddress()).isEqualTo("Address1");
    }

    @Test
    @DisplayName("Search Company Result Empty Test")
    void searchCompanyResultEmptyTest() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.fromString("DESC"), "createdAt"));

        // When
        Page<CompanyResponseDto> companyResponseDtoPage = companyService.searchCompany("CompanySearchFail", pageable);

        // Then
        assertThat(companyResponseDtoPage.getTotalPages()).isEqualTo(0);
    }

    @Test
    @DisplayName("Update Company Test")
    void updateCompanyTest() {
        // Given
        CompanyUpdateRequestDto requestDto = new CompanyUpdateRequestDto(
                "NameUpdateTest",
                "Description2",
                "Address2"
        );
        // When
        companyService.updateCompany(createdCompany.getId(), requestDto);

        // Then
        CompanyResponseDto companyResponseDto = companyService.getCompany(createdCompany.getId());
        assertThat(companyResponseDto).isNotNull();
        assertThat(companyResponseDto.getCompanyName()).isEqualTo("NameUpdateTest");
        assertThat(companyResponseDto.getDescription()).isEqualTo("Description2");
        assertThat(companyResponseDto.getAddress()).isEqualTo("Address2");
    }

    @Test
    @DisplayName("Update Company Fail Test")
    void updateCompanyFailTest() {
        // Given
        CompanyUpdateRequestDto requestDto = new CompanyUpdateRequestDto(
                "NameUpdateTestFaillllllll",
                "Description2",
                "Address2"
        );
        // When - Then
        assertThatThrownBy(() -> companyService.updateCompany(createdCompany.getId(), requestDto)).isInstanceOf(ApiException.class);
    }

}

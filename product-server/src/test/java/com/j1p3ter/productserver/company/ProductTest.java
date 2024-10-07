package com.j1p3ter.productserver.company;

import com.j1p3ter.productserver.application.CompanyService;
import com.j1p3ter.productserver.application.ProductService;
import com.j1p3ter.productserver.application.dto.company.CompanyCreateRequestDto;
import com.j1p3ter.productserver.application.dto.company.CompanyResponseDto;
import com.j1p3ter.productserver.application.dto.product.ProductCreateRequestDto;
import com.j1p3ter.productserver.application.dto.product.ProductOptionDto;
import com.j1p3ter.productserver.application.dto.product.ProductResponseDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductTest {

    @Autowired
    CompanyService companyService;

    @Autowired
    ProductService productService;

    @Autowired
    EntityManager em;

    CompanyResponseDto createdCompany;
    ProductResponseDto createdProduct;

    @BeforeEach
    void setUp(){
        CompanyCreateRequestDto companyCreateRequestDto = new CompanyCreateRequestDto(
                1L,
                "CompanyNameforTest",
                "Description1",
                "Address1"
        );

        createdCompany = companyService.createCompany(companyCreateRequestDto);

        ProductOptionDto productOptionDto = new ProductOptionDto(
                "Blue",
                "Color",
                100
        );

        List<ProductOptionDto> optionList = new ArrayList<>();
        optionList.add(productOptionDto);

        ProductCreateRequestDto productCreateRequestDto = new ProductCreateRequestDto(
                createdCompany.getId(),
                "ProductNameforTest",
                "DescriptionP",
                10000,
                8000,
                100,
                optionList,
                3000L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1L)
        );

        createdProduct = productService.createProduct(1L, productCreateRequestDto);
    }

    @Test
    @DisplayName("Create Product Test")
    void createProductTest() {
        //Given - When Setup 시 추가

        //Then
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getCompanyId()).isEqualTo(createdCompany.getId());
        assertThat(createdProduct.getProductName()).isEqualTo("ProductNameforTest");
        assertThat(createdProduct.getDescription()).isEqualTo("DescriptionP");
        assertThat(createdProduct.getProductOptions().get(0).getOptionName()).isEqualTo("Blue");
    }

}

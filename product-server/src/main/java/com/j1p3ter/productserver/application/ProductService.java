package com.j1p3ter.productserver.application;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.productserver.application.dto.product.ProductCreateRequestDto;
import com.j1p3ter.productserver.application.dto.product.ProductOptionDto;
import com.j1p3ter.productserver.application.dto.product.ProductResponseDto;
import com.j1p3ter.productserver.domain.company.Company;
import com.j1p3ter.productserver.domain.company.CompanyRepository;
import com.j1p3ter.productserver.domain.product.*;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j(topic = "Product Service")
public class ProductService {

    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponseDto createProduct(Long userId, ProductCreateRequestDto requestDto){
        Company company;
        try{
            company = companyRepository.findById(requestDto.getCompanyId()).orElseThrow();
            if(company.getUserId() != userId)
                throw new IllegalArgumentException("FORBIDDEN");
        }catch(IllegalArgumentException e){
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 Company가 아닙니다.", e.getMessage());
        }catch(Exception e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Company를 찾을 수 없습니다", e.getMessage());
        }

        Category category;
        try{
            category = categoryRepository.findById(requestDto.getCategoryCode()).orElseThrow();
        }catch (Exception e){
            throw new ApiException(HttpStatus.NOT_FOUND, "Category 를 찾을 수 없습니다", e.getMessage());
        }

        try{
            Product product = requestDto.toEntity(company, category);
            for(ProductOptionDto productOptionDto : requestDto.getProductOptions()){
                product.addProductOption(productOptionDto.toEntity(product));
            }
            return ProductResponseDto.from(productRepository.save(product));
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Product 생성에 실패했습니다.", e.getMessage());
        }
    }

}

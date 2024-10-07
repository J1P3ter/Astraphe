package com.j1p3ter.productserver.application;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.productserver.application.dto.product.ProductCreateRequestDto;
import com.j1p3ter.productserver.application.dto.product.ProductOptionDto;
import com.j1p3ter.productserver.application.dto.product.ProductResponseDto;
import com.j1p3ter.productserver.application.dto.product.ProductUpdateRequestDto;
import com.j1p3ter.productserver.domain.company.Company;
import com.j1p3ter.productserver.domain.company.CompanyRepository;
import com.j1p3ter.productserver.domain.product.Category;
import com.j1p3ter.productserver.domain.product.CategoryRepository;
import com.j1p3ter.productserver.domain.product.Product;
import com.j1p3ter.productserver.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        }catch(Exception e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Company를 찾을 수 없습니다", e.getMessage());
        }

        if(company.getUserId() != userId)
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 Company가 아닙니다.", "FORBIDDEN");

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

    public ProductResponseDto getProduct(Long productId){
        try{
            return ProductResponseDto.from(productRepository.findById(productId).orElseThrow());
        }catch (Exception e){
            throw new ApiException(HttpStatus.NOT_FOUND, "Product 를 찾을 수 없습니다", e.getMessage());
        }
    }

    public Page<ProductResponseDto> searchProduct(String companyName, String productName, Long categoryCode, Pageable pageable){
        if(!categoryCode.equals(0L)){ // category 0은 전체
            try{
                categoryRepository.findById(categoryCode).orElseThrow();
            }catch (Exception e){
                throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 Category 입니다", e.getMessage());
            }
        }

        try{
            if(!(companyName ==null)){
                Page<ProductResponseDto> productResponseDtos = productRepository.searchByCompanyName(companyName, categoryCode, pageable).map(ProductResponseDto::from);
                return productResponseDtos;
            }else{
                Page<ProductResponseDto> productResponseDtos = productRepository.searchByProductName(productName, categoryCode, pageable).map(ProductResponseDto::from);
                return productResponseDtos;
            }
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Product Search에 실패했습니다.", e.getMessage());
        }
    }


    @Transactional
    public ProductResponseDto updateProduct(Long userId, Long productId, ProductUpdateRequestDto requestDto){

        Category category;
        try{
            category = categoryRepository.findById(requestDto.getCategoryCode()).orElseThrow();
        }catch (Exception e){
            throw new ApiException(HttpStatus.NOT_FOUND, "Category 를 찾을 수 없습니다", e.getMessage());
        }

        Product product;
        try{
            product = productRepository.findById(productId).orElseThrow();
        }catch (Exception e){
            throw new ApiException(HttpStatus.NOT_FOUND, "Product 를 찾을 수 없습니다", e.getMessage());
        }

        if(product.getCompany().getUserId() != userId)
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 Product가 아닙니다.", "FORBIDDEN");

        try{
            product.clearProductOption();
            for(ProductOptionDto productOptionDto : requestDto.getProductOptions()){
                product.addProductOption(productOptionDto.toEntity(product));
            }
            product.updateProduct(
                    requestDto.getProductName(),
                    requestDto.getDescription(),
                    requestDto.getOriginalPrice(),
                    requestDto.getDiscountedPrice(),
                    requestDto.getStock(),
                    category,
                    requestDto.getSaleStartTime(),
                    requestDto.getSaleEndTime()
            );
            return ProductResponseDto.from(product);
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Product Update에 실패했습니다.", e.getMessage());
        }
    }

    @Transactional
    public String deleteProduct(Long userId, Long productId){
        Product product;
        try{
            product = productRepository.findById(productId).orElseThrow();
        }catch (Exception e){
            throw new ApiException(HttpStatus.NOT_FOUND, "Product 를 찾을 수 없습니다", e.getMessage());
        }

        if(product.getCompany().getUserId() != userId)
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 Product가 아닙니다.", "FORBIDDEN");

        try{
            product.softDelete(userId);
            return "Product is deleted";
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Product Delete에 실패했습니다.", e.getMessage());
        }

    }

}

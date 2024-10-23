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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j(topic = "Product Service")
public class ProductService {

    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // S3 서비스 이용을 위한 variables
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket-name}")
    String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    String region;

    final String PRODUCT_IMG_PATH = "product-images";
    // -------------------------------

    @Transactional
    public ProductResponseDto createProduct(Long userId, MultipartFile productImg, MultipartFile productDescriptionImg, ProductCreateRequestDto requestDto){
        if(!isCorrectPrices(requestDto.getOriginalPrice(), requestDto.getDiscountedPrice()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "할인 가격보다 원가가 작을 수 없습니다.", "Original price is lower than discounted price");

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

        // S3 이미지 업로드
        String productImgUrl = null;
        String productDescriptionImgUrl = null;
        if(productImg != null){
            productImgUrl = getS3UploadUrl(productImg.getOriginalFilename());
        }
        if(productDescriptionImg != null){
            productDescriptionImgUrl = getS3UploadUrl(productDescriptionImg.getOriginalFilename());
        }

        uploadImages(productImgUrl, productImg, productDescriptionImgUrl, productDescriptionImg);

        String UPLOADED_URL = "https://" + bucketName + ".s3." +region + ".amazonaws.com/";
        productImgUrl = productImgUrl == null ? null : UPLOADED_URL + productImgUrl;
        productDescriptionImgUrl = productDescriptionImgUrl == null ? null : UPLOADED_URL + productDescriptionImgUrl;

        try{
            Product product = requestDto.toEntity(company, category, productImgUrl,  productDescriptionImgUrl);
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
    public ProductResponseDto updateProduct(Long userId, Long productId, MultipartFile productImg, MultipartFile productDescriptionImg, ProductUpdateRequestDto requestDto){
        if(!isCorrectPrices(requestDto.getOriginalPrice(), requestDto.getDiscountedPrice()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "할인 가격보다 원가가 작을 수 없습니다.", "Original price is lower than discounted price");

        Category category;
        try{
            category = categoryRepository.findById(requestDto.getCategoryCode()).orElseThrow();
        }catch (Exception e){
            throw new ApiException(HttpStatus.NOT_FOUND, "Category 를 찾을 수 없습니다", e.getMessage());
        }

        Product product = getProductById(productId);

        if(product.getCompany().getUserId() != userId)
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 Product가 아닙니다.", "FORBIDDEN");

        // S3 이미지 업로드
        String productImgUrl = null;
        String productDescriptionImgUrl = null;
        if(productImg != null){
            productImgUrl = getS3UploadUrl(productImg.getOriginalFilename());
        }
        if(productDescriptionImg != null){
            productDescriptionImgUrl = getS3UploadUrl(productDescriptionImg.getOriginalFilename());
        }

        uploadImages(productImgUrl, productImg, productDescriptionImgUrl, productDescriptionImg);

        String UPLOADED_URL = "https://" + bucketName + ".s3." +region + ".amazonaws.com/";
        productImgUrl = productImgUrl == null ? null : UPLOADED_URL + productImgUrl;
        productDescriptionImgUrl = productDescriptionImgUrl == null ? null : UPLOADED_URL + productDescriptionImgUrl;

        try{
            product.clearProductOption();
            for(ProductOptionDto productOptionDto : requestDto.getProductOptions()){
                product.addProductOption(productOptionDto.toEntity(product));
            }
            product.updateProduct(
                    requestDto.getProductName(),
                    productImgUrl,
                    requestDto.getDescription(),
                    productDescriptionImgUrl,
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
        Product product = getProductById(productId);

        if(product.getCompany().getUserId() != userId)
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 Product가 아닙니다.", "FORBIDDEN");

        try{
            product.softDelete(userId);
            return "Product is deleted";
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Product Delete에 실패했습니다.", e.getMessage());
        }

    }

    @Transactional
    public void reduceStock(Long productId, int quantity) {
        if(quantity <= 0)
            throw new ApiException(HttpStatus.BAD_REQUEST, "quantity 가 1 이상이어야 합니다.", "quantity <= 0");

        Product product = getProductById(productId);

        if(product.getStock() - quantity < 0)
            throw new ApiException(HttpStatus.BAD_REQUEST, "재고가 충분하지 않습니다.", "Not enough stock");

        try{
            product.reduceStock(quantity);
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "재고 감소 실패", e.getMessage());
        }

    }

    @Transactional
    public void addStock(Long productId, int quantity) {
        if(quantity <= 0)
            throw new ApiException(HttpStatus.BAD_REQUEST, "quantity 가 1 이상이어야 합니다.", "quantity <= 0");

        Product product = getProductById(productId);

        try{
            product.addStock(quantity);
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "재고 추가 실패", e.getMessage());
        }
    }

    public Product getProductById(Long productId){
        try{
            return productRepository.findById(productId).orElseThrow();
        }catch (Exception e){
            throw new ApiException(HttpStatus.NOT_FOUND, "Product 를 찾을 수 없습니다", e.getMessage());
        }
    }

    public boolean isCorrectPrices(int originalPrice, int discountedPrice){
        if(originalPrice >= discountedPrice) return true;
        else return false;
    }

    private String getS3UploadUrl(String fileName) {
        return PRODUCT_IMG_PATH + "/" + fileName;
    }

    @SneakyThrows
    private void putObjectToS3(String uploadUrl, MultipartFile multipartFile){
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uploadUrl)
                .build();

        RequestBody content = RequestBody.fromInputStream(
                multipartFile.getInputStream(),
                multipartFile.getSize()
        );

        s3Client.putObject(request, content);
    }

    private void uploadImages(String productImgUploadUrl, MultipartFile productImg,
                              String productDescriptionImgUploadUrl, MultipartFile productDescriptionImg){
        try{
            if(productImg != null){
                putObjectToS3(productImgUploadUrl, productImg);
            }
            if(productDescriptionImg != null){
                putObjectToS3(productDescriptionImgUploadUrl, productDescriptionImg);
            }
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 Image Upload 과정에서 실패했습니다.", e.getMessage());
        }
    }
}

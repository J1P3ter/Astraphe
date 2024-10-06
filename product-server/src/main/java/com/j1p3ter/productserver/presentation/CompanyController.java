package com.j1p3ter.productserver.presentation;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.productserver.application.CompanyService;
import com.j1p3ter.productserver.application.dto.company.CompanyCreateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Company API")
@Slf4j(topic = "Company Controller")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "Create Company")
    @PostMapping
    public ApiResponse<?> createCompany(
            @RequestHeader(name = "X-USER-ID", required = false) String userId,
            @RequestBody CompanyCreateRequestDto companyCreateRequestDto
            ){
        return ApiResponse.success(companyService.createCompany(companyCreateRequestDto));
    }

    @Operation(summary = "Get Company Info")
    @GetMapping("/{companyId}")
    public ApiResponse<?> getCompanyInfo(
            @RequestHeader(name = "X-USER-ID", required = false) String userId,
            @PathVariable Long companyId
    ) {
        return ApiResponse.success(companyService.getCompany(companyId));
    }

    @Operation(summary = "Search Company by Company Name")
    @GetMapping
    public ApiResponse<?> searchCompany(
            @RequestHeader(name = "X-USER-ID", required = false) String userId,
            @RequestParam(defaultValue = "", name = "companyName", required = false) String companyName,
            @RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(defaultValue = "createdAt", name = "sort") String sort,
            @RequestParam(defaultValue = "DESC", name = "direction") String direction
    ){
        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.fromString(direction), sort));
        return ApiResponse.success(companyService.searchCompany(companyName, pageable));
    }
}

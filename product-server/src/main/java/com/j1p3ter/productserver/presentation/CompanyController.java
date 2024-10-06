package com.j1p3ter.productserver.presentation;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.productserver.application.CompanyService;
import com.j1p3ter.productserver.application.dto.company.CompanyCreateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
